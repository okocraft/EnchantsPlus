package net.okocraft.enchantsplus.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTickEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerTickEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
