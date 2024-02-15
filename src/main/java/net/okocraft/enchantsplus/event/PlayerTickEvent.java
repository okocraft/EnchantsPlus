package net.okocraft.enchantsplus.event;

import lombok.Getter;
import net.okocraft.enchantsplus.model.EnchantsPlusPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTickEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final EnchantsPlusPlayer enchantsPlusPlayer;
    @Getter
    private final long tickCount;

    public PlayerTickEvent(Player bukkitPlayer, EnchantsPlusPlayer enchantsPlusPlayer, long tickCount) {
        super(bukkitPlayer);
        this.enchantsPlusPlayer = enchantsPlusPlayer;
        this.tickCount = tickCount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
