package net.okocraft.enchantsplus.event;

import java.util.Map;

import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemCustomEnchantEvent extends Event implements Cancellable {
    private final HandlerList handlers = new HandlerList();
    private final Player enchanter;
    private final LocalItemStack enchantedItem;
    private final Map<EnchantPlus, Integer> enchantsToAdd;
    private boolean isCancelled;
    private final EnchantingType type;
    private boolean ignoreAbsoluteMaxEnchants;

    public ItemCustomEnchantEvent(Player enchanter, LocalItemStack enchantedItem, Map<EnchantPlus, Integer> enchantsToAdd, EnchantingType type, boolean ignoreAbsoluteMaxEnchants) {
        this.enchanter = enchanter;
        this.enchantedItem = enchantedItem;
        this.enchantsToAdd = enchantsToAdd;
        this.isCancelled = false;
        this.type = type;
        this.ignoreAbsoluteMaxEnchants = ignoreAbsoluteMaxEnchants;
    }

    public Player getEnchanter() {
        return this.enchanter;
    }

    public LocalItemStack getItem() {
        return this.enchantedItem;
    }

    public Map<EnchantPlus, Integer> getEnchantsToAdd() {
        return this.enchantsToAdd;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public HandlerList getHandlerList() {
        return handlers;
    }

    public boolean getIgnoreAbsoluteMaxEnchants() {
        return this.ignoreAbsoluteMaxEnchants;
    }

    public void setIgnoreAbsoluteMaxEnchants(boolean ignoreAbsoluteMaxEnchants) {
        this.ignoreAbsoluteMaxEnchants = ignoreAbsoluteMaxEnchants;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public EnchantingType getEnchantingType() {
        return this.type;
    }
}
