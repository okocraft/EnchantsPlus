package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.RejuvinationConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Rejuvination extends TickDependentEnchantHandler<RejuvinationConfig> {

    public Rejuvination(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getRejuvinationConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!checkTickInterval(player, 20)) {
            return;
        }

        if (!config.isEnabled()) {
            return;
        }

        if (isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!config.worksInHand() && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                continue;
            }

            LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
            if (!isValidTool(item) || item.getItemMeta().isUnbreakable()) {
                continue;
            }

            int level = item.getCustomEnchantLevel(EnchantPlus.REJUVINATION);
            if (random.nextDouble() < config.getRegenRate(level)) {
                ItemMeta meta = item.getItemMeta();

                Damageable damageable = (Damageable) meta;
                if (damageable.hasDamage()) {
                    ((Damageable) meta).setDamage(damageable.getDamage() - 1);
                    item.setItemMeta(meta);
                    player.getEquipment().setItem(slot, item.getItem());
                }
            }
        }
    }
}
