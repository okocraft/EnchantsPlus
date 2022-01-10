package net.okocraft.enchantsplus.enchant.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.EnchantConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;

import org.bukkit.entity.Player;

public abstract class TickDependentEnchantHandler<C extends EnchantConfig> extends EnchantPlusHandler<C, PlayerTickEvent> {

    private final Map<UUID, Integer> tickCounts = new HashMap<>();

    public TickDependentEnchantHandler(EnchantsPlus plugin, C config) {
        super(plugin, config);
    }

    @Override
    public Class<PlayerTickEvent> getEventClass() {
        return PlayerTickEvent.class;
    }

    protected boolean checkTickInterval(Player player, int interval) {
        int tickCount = tickCounts.getOrDefault(player.getUniqueId(), 0);
        tickCount++;
        tickCount %= interval;
        if (tickCount == 0) {
            tickCounts.remove(player.getUniqueId());
            return true;
        } else {
            tickCounts.put(player.getUniqueId(), tickCount);
            return false;
        }
    }

    public void removeTickCount(Player player) {
        tickCounts.remove(player.getUniqueId());
    }
}
