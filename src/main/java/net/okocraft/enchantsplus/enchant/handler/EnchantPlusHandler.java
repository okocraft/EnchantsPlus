package net.okocraft.enchantsplus.enchant.handler;

import java.util.Random;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.ArmorEnchantConfig;
import net.okocraft.enchantsplus.config.Config.EnchantConfig;
import net.okocraft.enchantsplus.config.Config.GeneralConfig;
import net.okocraft.enchantsplus.enchant.EnchantmentTarget;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class EnchantPlusHandler<C extends EnchantConfig, T extends Event> {

    protected final Random random = new Random();
    protected final EnchantsPlus plugin;
    protected final GeneralConfig generalConfig;
    protected final C config;

    public EnchantPlusHandler(EnchantsPlus plugin, C config) {
        this.plugin = plugin;
        this.generalConfig = plugin.getMainConfig().getGeneralConfig();
        this.config = config;
    }

    public abstract Class<T> getEventClass();

    public abstract void handle(T event);

    protected boolean isDisabledByWorldGuard(LivingEntity user, Location location) {
        return plugin.getBridgeManager().getWorldguardBridge().getDisabledEnchants(user, location).contains(config.getType());
    }

    protected boolean isValidTool(LocalItemStack item) {
        return item != null && item.getType() != Material.ENCHANTED_BOOK && item.hasCustomEnchant(config.getType());
    }

    protected PlayerItemDamageEvent callItemDamageEvent(Player player, ItemStack what, int damage) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return null;
        }
        ItemMeta meta = what.getItemMeta();
        if (meta == null || meta.isUnbreakable()) {
            return null;
        }

        damage = reduceDamageWithUnbreaking(what, damage);

        PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, what, damage);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return event;
        }
        
        ItemStack clone = what.clone();
        boolean broken = useItem(player, what, event.getDamage());
        var inventory = player.getInventory();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotItem = inventory.getItem(slot);
            // EntityEquipment#getItem(Equipment) returns **null** when the armor is not set.
            // THIS IS BUKKIT API. WE MUST ALWAYS CHECK THEIR IMPLEMENTATIONS.
            // noinspection ConstantConditions
            if (slotItem != null && slotItem.isSimilar(clone)) {
                if (broken) {
                    // TODO: 壊れたときの挙動をデバッグする
                    player.playEffect(EntityEffect.valueOf("BREAK_EQUIPMENT_" + slot.name()));
                }
                inventory.setItem(slot, what);
                break;
            }
        }
        
        return event;
    }

    /**
     * Decrease item durability and if broken call PlayerItemBreakEvent.
     * A state of passed itemstack will be changed:
     * When broken, amount is decreased by one and reset durability.
     * When not broken, simply, durability is decreased.
     * 
     * @param player used player.
     * @param item used item.
     * @param damage given to item.
     * @return When item broke, returns true. Otherwise false.
     */
    private boolean useItem(Player player, ItemStack item, int damage) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        Damageable damageable = (Damageable) meta;
        if (item.getType().getMaxDurability() - damageable.getDamage() <= damage) {
            callItemBreakEvent(player, item);
            damageable.setDamage(0);
            item.setItemMeta(meta);
            item.setAmount(item.getAmount() - 1);
            return true;
        } else {
            damageable.setDamage(damageable.getDamage() + damage);
            item.setItemMeta(meta);
            return false;
        }
    }

    protected PlayerItemBreakEvent callItemBreakEvent(Player player, ItemStack item) {
        PlayerItemBreakEvent event = new PlayerItemBreakEvent(player, item);
        plugin.getServer().getPluginManager().callEvent(event);
        
        return event;
    }

    public static int reduceDamageWithUnbreaking(ItemStack itemstack, int damage) {
        int result = damage;
        Random random = new Random();
        for (int i = 0; i < damage; i++) {
            if (shouldIgnoreDurabilityDrop(itemstack, itemstack.getEnchantmentLevel(Enchantment.DURABILITY), random)) {
                result--;
            }
        }
        return result;
    }

    public static boolean shouldIgnoreDurabilityDrop(ItemStack itemstack, int level, Random random) {
        return (!EnchantmentTarget.ARMOR.includes(itemstack) || !(random.nextFloat() < 0.6F)) && random.nextInt(level + 1) > 0;
    }

    protected EquipmentSlot chooseValidItemSlot(EntityEquipment equipment, EquipmentSlot... slots) {
        for (EquipmentSlot slot : slots) {
            if (config instanceof ArmorEnchantConfig && !((ArmorEnchantConfig) config).worksInHand()
                    && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                continue;
            }
            if (isValidTool(plugin.wrapItem(equipment.getItem(slot)))) {
                return slot;
            }
        }
        return null;
    }

    protected LocalItemStack getValidItemFromSlots(EntityEquipment eqipment, EquipmentSlot... slots) {
        EquipmentSlot slot = chooseValidItemSlot(eqipment, slots);
        if (slot == null) {
            return null;
        }
        return plugin.wrapItem(eqipment.getItem(slot));
    }
}
