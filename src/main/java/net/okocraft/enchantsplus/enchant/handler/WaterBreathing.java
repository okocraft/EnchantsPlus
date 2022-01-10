package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.WaterBreathingConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WaterBreathing extends TickDependentEnchantHandler<WaterBreathingConfig> {

    public WaterBreathing(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getWaterBreathingConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!config.isEnabled() || !checkTickInterval(player, 200)
                || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        LocalItemStack item = getValidItemFromSlots(player.getEquipment(), EquipmentSlot.HEAD, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
        if (item != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 260, 0));
        }

    }
}
