package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.UpdraftConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class Updraft extends TickDependentEnchantHandler<UpdraftConfig> {

    public Updraft(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getUpdraftConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isGliding() || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        if (chooseValidItemSlot(player.getEquipment(), EquipmentSlot.CHEST, EquipmentSlot.HAND,
                EquipmentSlot.OFF_HAND) == null) {
            return;
        }

        float pitch = player.getLocation().getPitch();
        Vector velocity = player.getVelocity();
        if (pitch > 0 || velocity.getY() > 0.4) {
            return;
        }

        velocity.setY(velocity.getY() + 0.090 * (pitch / -90));
        player.setVelocity(velocity);

    }
}
