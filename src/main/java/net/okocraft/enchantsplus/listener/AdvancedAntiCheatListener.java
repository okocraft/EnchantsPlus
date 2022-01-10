package net.okocraft.enchantsplus.listener;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdvancedAntiCheatListener implements Listener {

    private final EnchantsPlus plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onViolateProtection(PlayerViolationEvent event) {
        Player player = event.getPlayer();

        HackType hackType = event.getHackType();
        if (hackType == HackType.INTERACT || hackType == HackType.FASTBREAK) {
            LocalItemStack handItem = plugin.wrapItem(player.getEquipment().getItemInMainHand());
            if (handItem != null && (handItem.hasCustomEnchant(EnchantPlus.EXCAVATION)
                    || handItem.hasCustomEnchant(EnchantPlus.REINFORCED))) {
                event.setCancelled(true);
            }

        } else if (hackType == HackType.NOFALL) {
            LocalItemStack boots = plugin.wrapItem(player.getEquipment().getBoots());
            if (boots != null && boots.hasCustomEnchant(EnchantPlus.UPDRAFT)) {
                event.setCancelled(true);
            }
        }

    }
}
