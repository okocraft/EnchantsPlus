package net.okocraft.enchantsplus.enchant.handler;

import java.util.ArrayList;
import java.util.List;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.SoulboundConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Soulbound extends EnchantPlusHandler<SoulboundConfig, PlayerDeathEvent> {

    public Soulbound(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getSoulboundConfig());
    }

    @Override
    public Class<PlayerDeathEvent> getEventClass() {
        return PlayerDeathEvent.class;
    }

    @Override
    public void handle(PlayerDeathEvent event) {
        if (!config.isEnabled() || event.getKeepInventory()) {
            return;
        }

        Player user = event.getEntity();
        if (isDisabledByWorldGuard(user, user.getLocation())) {
            return;
        }

        List<ItemStack> kept = new ArrayList<>();
        for (ItemStack drop : user.getInventory().getContents()) {
            
            LocalItemStack dropLocal = plugin.wrapItem(drop);
            if (isValidTool(dropLocal)) {
                if (config.dissapearsAfterDeath()) {
                    dropLocal.removeCustomEnchant(EnchantPlus.SOULBOUND);
                }
                kept.add(dropLocal.getItem());
                event.getDrops().remove(drop);
            }
        }
        plugin.getPlayerData().addAllSoulBounded(user.getUniqueId(), kept);
    }

    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> soulbounded = plugin.getPlayerData().getSoulbounded(player.getUniqueId());
        if (soulbounded.isEmpty()) {
            return;
        }
        Location eyeLoc = player.getEyeLocation();
        for (ItemStack drop : player.getInventory().addItem(soulbounded.toArray(ItemStack[]::new)).values()) {
            player.getWorld().dropItem(eyeLoc, drop).setVelocity(eyeLoc.getDirection().multiply(0.25));
        }
        plugin.getPlayerData().clearSoulbounded(player.getUniqueId());
    }
}
