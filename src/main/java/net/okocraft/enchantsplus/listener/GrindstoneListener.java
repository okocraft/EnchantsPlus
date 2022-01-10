package net.okocraft.enchantsplus.listener;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GrindstoneListener implements Listener {

    private final EnchantsPlus plugin;
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        if (!(topInv instanceof GrindstoneInventory)) {
            return;
        }
        GrindstoneInventory grindstoneInventory = (GrindstoneInventory) topInv;
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalItemStack result = plugin.wrapItem(grindstoneInventory.getItem(2));
                if (result != null) {
                    result.useGrindstone();
                    grindstoneInventory.setItem(2, result.getItem());
                }
            }
        }.runTask(plugin);
    }
}
