package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.RegenerationConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.EquipmentSlot;

public class Regeneration extends TickDependentEnchantHandler<RegenerationConfig> {

    public Regeneration(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getRegenerationConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        if (!config.isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!checkTickInterval(player, 20) ||
                isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }
        int totalLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!config.worksInHand() && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                continue;
            }
            LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
            if (!isValidTool(item)) {
                continue;
            }

            totalLevel += item.getCustomEnchantLevel(EnchantPlus.REGENERATION);
        }

        if (random.nextDouble() > totalLevel * config.getHealRatePerLevel()) {
            return;
        }

        var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute == null) {
            return;
        }

        double health = player.getHealth();
        double maxHealth = attribute.getValue();

        if (health >= maxHealth) {
            return;
        }

        double healAmount = Math.min(maxHealth - health, config.getHealAmount());
        EntityRegainHealthEvent regainHealthEvent = new EntityRegainHealthEvent(player, healAmount, RegainReason.CUSTOM);
        plugin.getServer().getPluginManager().callEvent(regainHealthEvent);
        if (!regainHealthEvent.isCancelled()) {
            player.setHealth(Math.max(0, Math.min(player.getHealth() + regainHealthEvent.getAmount(), maxHealth)));
        }
    }
}
