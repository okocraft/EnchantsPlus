package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.bridge.nocheatplus.CheckType;
import net.okocraft.enchantsplus.config.Config.ExcavationConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.enchant.EnchantmentTarget;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Excavation extends EnchantPlusHandler<ExcavationConfig, BlockBreakEvent> {

    private static final CustomTag EMPTY_TAG = new CustomTag(Collections.emptySet(), "empty_tag");
    private static final CustomTag CUSTOM_MINEABLE_HOE;
    private static final CustomTag CUSTOM_MINEABLE_AXE;

    static {
        var hoeMaterials = EnumSet.copyOf(Tag.MINEABLE_HOE.getValues());
        hoeMaterials.addAll(Tag.CROPS.getValues());
        hoeMaterials.add(Material.NETHER_WART);
        CUSTOM_MINEABLE_HOE = new CustomTag(hoeMaterials, "custom_mineable_hoe");

        var axeMaterials = EnumSet.copyOf(Tag.MINEABLE_AXE.getValues());
        axeMaterials.removeIf(hoeMaterials::contains);
        CUSTOM_MINEABLE_AXE = new CustomTag(axeMaterials, "custom_mineable_axe");
    }

    private final Set<Block> excavatedBlocks = new HashSet<>();

    public Excavation(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getExcavationConfig());
    }

    @Override
    public Class<BlockBreakEvent> getEventClass() {
        return BlockBreakEvent.class;
    }

    @Override
    public void handle(BlockBreakEvent event) {
        Player user = event.getPlayer();
        Block block = event.getBlock();
        if (excavatedBlocks.contains(block)) {
            excavatedBlocks.remove(block);
            if (isDisabledByWorldGuard(user, block.getLocation())) {
                event.setCancelled(true);
            }
            if (excavatedBlocks.isEmpty()) {
                plugin.getBridgeManager().getNoCheatPlusBridge().unexempt(event.getPlayer(), CheckType.BLOCKBREAK);
            }

            return;
        }

        if (event.isCancelled() || !config.isEnabled() || isDisabledByWorldGuard(user, block.getLocation())) {
            return;
        }
        if (user.isSneaking()) {
            return;
        }

        LocalItemStack handItem = plugin.wrapItem(user.getInventory().getItemInMainHand());
        if (!isValidTool(handItem)) {
            return;
        }

        plugin.getBridgeManager().getNoCheatPlusBridge().exempt(user, CheckType.BLOCKBREAK);

        BlockFace playerFace = user.getFacing();
        float pitch = user.getLocation().getPitch();
        if (pitch < -45) {
            playerFace = BlockFace.UP;
        } else if (pitch > 45) {
            playerFace = BlockFace.DOWN;
        }

        List<BlockFace> relativeFaces = new ArrayList<>();
        for (BlockFace face : BlockFace.values()) {
            if (face.isCartesian() && face != playerFace && face.getOppositeFace() != playerFace) {
                relativeFaces.add(face);
            }
        }

        int level = handItem.getCustomEnchantLevel(EnchantPlus.EXCAVATION);

        ArrayList<Block> blocksToBreak = new ArrayList<>();
        blocksToBreak.add(block);
        for (BlockFace relativeFace1 : relativeFaces) {
            // select blocks like below:
            //  □ 
            // □ □
            //  □
            Block relative1 = block.getRelative(relativeFace1);
            blocksToBreak.add(relative1);

            for (BlockFace relativeFace2 : relativeFaces) {
                // select blocks like below:
                // □ □
                // 
                // □ □
                if (relativeFace1 == relativeFace2 || relativeFace1.getOppositeFace() == relativeFace2) {
                    continue;
                }

                Block relative2 = relative1.getRelative(relativeFace2);
                if (!blocksToBreak.contains(relative2)) {
                    blocksToBreak.add(relative2);
                }
            }
        }

        for (Block blockToBreak : blocksToBreak) {
            if (!blockToBreak.equals(event.getBlock()) && isPreferredTool(blockToBreak, handItem.getItem())) {
                excavatedBlocks.add(blockToBreak);
                user.breakBlock(blockToBreak);
            }

            for (int i = 1; i < level; i++) {
                blockToBreak = blockToBreak.getRelative(playerFace);
                if (isPreferredTool(blockToBreak, handItem.getItem())) {
                    excavatedBlocks.add(blockToBreak);
                    user.breakBlock(blockToBreak);
                }
            }
        }
    }

    private boolean isPreferredTool(@NotNull Block block, @NotNull ItemStack tool) {
        return block.isPreferredTool(tool) &&
                getBlockTag(tool.getType()).isTagged(block.getType());
    }

    private @NotNull Tag<Material> getBlockTag(@NotNull Material tool) {
        if (EnchantmentTarget.SHOVEL.includes(tool)) {
            return Tag.MINEABLE_SHOVEL;
        } else if (EnchantmentTarget.PICKAXE.includes(tool)) {
            return Tag.MINEABLE_PICKAXE;
        } else if (EnchantmentTarget.HOE.includes(tool)) {
            return CUSTOM_MINEABLE_HOE;
        } else if (EnchantmentTarget.AXE.includes(tool)) {
            return CUSTOM_MINEABLE_AXE;
        } else {
            return EMPTY_TAG;
        }
    }

    private record CustomTag(@NotNull Set<Material> materials, @NotNull String name) implements Tag<Material> {

        @Override
        public boolean isTagged(@NotNull Material material) {
            return materials.contains(material);
        }

        @Override
        public @NotNull Set<Material> getValues() {
            return materials;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return new NamespacedKey(EnchantsPlus.getInstance(), name);
        }
    }
}
