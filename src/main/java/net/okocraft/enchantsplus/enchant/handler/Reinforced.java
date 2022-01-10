package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.bridge.nocheatplus.CheckType;
import net.okocraft.enchantsplus.config.Config.ReinforcedConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

public class Reinforced extends EnchantPlusHandler<ReinforcedConfig, BlockDamageEvent> {

    public Reinforced(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getReinforcedConfig());
    }

    @Override
    public Class<BlockDamageEvent> getEventClass() {
        return BlockDamageEvent.class;
    }

    @Override
    public void handle(BlockDamageEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return;
        }

        Player user = event.getPlayer();
        if (plugin.getBridgeManager().getVeinMinerBridge().isVeinMining(user)) {
            return;
        }

        LocalItemStack handItem = plugin.wrapItem(user.getInventory().getItemInMainHand());
        Block block = event.getBlock();
        if (!isValidTool(handItem) || isDisabledByWorldGuard(user, block.getLocation())
                || !isReinforcedTarget(block.getType()) || !block.isPreferredTool(handItem.getItem())) {
            return;
        }

        int level = handItem.getCustomEnchantLevel(EnchantPlus.REINFORCED);

        plugin.getBridgeManager().getNoCheatPlusBridge().exempt(user, CheckType.BLOCKBREAK);
        user.breakBlock(block);
        if (config.getUnbreakingRate(level) < random.nextDouble()) {
            callItemDamageEvent(user, handItem.getItem(), config.getDurabilityCost() - 1);
        }
        plugin.getBridgeManager().getNoCheatPlusBridge().unexempt(user, CheckType.BLOCKBREAK);
    }

    private static boolean isReinforcedTarget(Material type) {
        return switch (type) {
            case COAL_ORE, DIAMOND_ORE, EMERALD_ORE, GOLD_ORE, IRON_ORE,
                    LAPIS_ORE, REDSTONE_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_DIAMOND_ORE,
                    DEEPSLATE_EMERALD_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_IRON_ORE,
                    DEEPSLATE_LAPIS_ORE, DEEPSLATE_REDSTONE_ORE, NETHER_QUARTZ_ORE -> true;
            default -> false;
        };
    }
}
