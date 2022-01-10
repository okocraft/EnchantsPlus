package net.okocraft.enchantsplus.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MainTimerTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
}
