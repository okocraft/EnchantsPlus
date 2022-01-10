package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.AutoJumpConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AutoJump extends TickDependentEnchantHandler<AutoJumpConfig> {

    public AutoJump(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getAutoJumpConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!config.isEnabled() || !checkTickInterval(player, 200)
                || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        LocalItemStack item = getValidItemFromSlots(player.getEquipment(), EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
        if (item != null) {
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.JUMP, 260, item.getCustomEnchantLevel(EnchantPlus.AUTO_JUMP) - 1));
        }

    }
}
