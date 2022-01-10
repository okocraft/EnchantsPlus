package net.okocraft.enchantsplus.enchant.handler;

import java.util.Iterator;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.RegainConfig;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Regain extends EnchantPlusHandler<RegainConfig, BlockDropItemEvent> {

    public Regain(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getRegainConfig());
    }

    @Override
    public Class<BlockDropItemEvent> getEventClass() {
        return BlockDropItemEvent.class;
    }

    @Override
    public void handle(BlockDropItemEvent event) {
        Player user = event.getPlayer();
        if (event.isCancelled() || !config.isEnabled() || user.isSneaking()) {
            return;
        }
        Block block = event.getBlock();        
        if (isDisabledByWorldGuard(user, block.getLocation())) {
            return;
        }

        LocalItemStack handItem = plugin.wrapItem(user.getInventory().getItemInMainHand().clone());
        if (!isValidTool(handItem)) {
            return;
        }

        ItemMeta meta = handItem.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        int maxDurability = handItem.getType().getMaxDurability();
        final int recoveringDamage = Math.min(maxDurability / 10, damageable.getDamage());

        boolean repaired = false;
        Iterator<Item> dropIt = event.getItems().iterator();
        while (dropIt.hasNext()) {
            Item dropItem = dropIt.next();
            ItemStack drop = dropItem.getItemStack();
            if (checkItemRepairable(handItem, drop.getType())) {
                if (random.nextDouble() < 0.3) {
                    if (drop.getAmount() == 1) {
                        dropIt.remove();
                    } else {
                        drop.setAmount(drop.getAmount() - 1);
                        dropItem.setItemStack(drop);
                    }
                }
                damageable.setDamage(damageable.getDamage() - recoveringDamage);
                repaired = true;
                break;
            }
        }
        if (!repaired) {
            return;
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            if (soundVolume > 0.2F) {
                soundVolume -= 0.2F;
            }
            user.getWorld().playSound(user.getEyeLocation(), Sound.BLOCK_ANVIL_LAND, soundVolume, 1.25F);
        }
        
        handItem.setItemMeta(damageable);
        user.getInventory().setItemInMainHand(handItem.getItem());
    }

    private boolean checkItemRepairable(LocalItemStack item, Material material) {
        if (config.getDisabledMaterials().contains(material)) {
            return false;
        }
        if (item.getType() == Material.WOODEN_PICKAXE) {
            return material == Material.ACACIA_LOG
                || material == Material.BIRCH_LOG
                || material == Material.DARK_OAK_LOG
                || material == Material.JUNGLE_LOG
                || material == Material.OAK_LOG
                || material == Material.SPRUCE_LOG
                || material == Material.CRIMSON_STEM
                || material == Material.WARPED_STEM;

        } else if (item.getType() == Material.IRON_PICKAXE) {
            return material == Material.IRON_ORE
                || material == Material.IRON_INGOT
                || material == Material.DEEPSLATE_IRON_ORE
                || material == Material.RAW_IRON;

        } else if (item.getType() == Material.GOLDEN_PICKAXE) {
            return material == Material.GOLD_ORE
                || material == Material.GOLD_INGOT
                || material == Material.DEEPSLATE_GOLD_ORE
                || material == Material.RAW_GOLD;

        } else if (item.getType() == Material.DIAMOND_PICKAXE) {
            return material == Material.DIAMOND_ORE
                || material == Material.DIAMOND
                || material == Material.DEEPSLATE_DIAMOND_ORE;

        } else if (item.getType() == Material.STONE_PICKAXE) {
            return material == Material.COBBLESTONE
                || material == Material.STONE
                || material == Material.COBBLED_DEEPSLATE
                || material == Material.DEEPSLATE
                || material == Material.BLACKSTONE;
        } else if (item.getType() == Material.NETHERITE_PICKAXE) {
            return material == Material.NETHERITE_INGOT
                || material == Material.ANCIENT_DEBRIS;
        } else {
            return false;
        }
    }

}
