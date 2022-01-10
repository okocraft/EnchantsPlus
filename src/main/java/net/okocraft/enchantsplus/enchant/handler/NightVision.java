package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.NightVisionConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision extends TickDependentEnchantHandler<NightVisionConfig> {

    public NightVision(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getNightVisionConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!config.isEnabled() || !checkTickInterval(player, 20)
                || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        LocalItemStack item = getValidItemFromSlots(player.getEquipment(), EquipmentSlot.HEAD, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
        if (item != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0));
        }

    }
}
