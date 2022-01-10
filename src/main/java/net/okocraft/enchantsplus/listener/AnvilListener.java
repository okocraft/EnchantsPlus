package net.okocraft.enchantsplus.listener;

import java.util.HashMap;
import java.util.Map;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Languages.Language;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.enchant.EnchantAPI.MaxEnchantCheckResult;
import net.okocraft.enchantsplus.event.EnchantingType;
import net.okocraft.enchantsplus.event.ItemCustomEnchantEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnvilListener implements Listener {

    private final EnchantsPlus plugin;

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        LocalItemStack result = plugin.wrapItem(event.getResult());
        if (result == null) {
            return;
        }
        result = result.clone();

        LocalItemStack item1 = plugin.wrapItem(event.getInventory().getItem(0));
        if (item1 == null) {
            return;
        }
        item1 = item1.clone();

        result.clearCustomEnchant();
        result.addCustomEnchants(item1.getCustomEnchants(), false);

        LocalItemStack item2 = plugin.wrapItem(event.getInventory().getItem(1));
        if (item2 != null) {
            item2 = item2.clone();
            result.combineCustomEnchants(item2.getCustomEnchants());
        }
        event.setResult(result.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player)) {
            return;
        }
        Inventory inv = event.getClickedInventory();
        if (!(inv instanceof AnvilInventory) || event.getSlot() != 2) {
            return;
        }

        LocalItemStack item1 = plugin.wrapItem(event.getInventory().getItem(0)).clone();
        LocalItemStack item2 = plugin.wrapItem(event.getInventory().getItem(1)).clone();
        LocalItemStack result = plugin.wrapItem(event.getInventory().getItem(2)).clone();
        if (item1 == null || item2 == null || item2.getCustomEnchants().isEmpty()) {
            return;
        }

        Map<EnchantPlus, Integer> item1Enchants = item1.getCustomEnchants();
        Map<EnchantPlus, Integer> resultEnchants = new HashMap<>(result.getCustomEnchants());

        resultEnchants.keySet().removeIf(enchant -> item1Enchants.containsKey(enchant)
                && item1Enchants.get(enchant) >= resultEnchants.get(enchant));

        ItemCustomEnchantEvent enchantEvent = new ItemCustomEnchantEvent((Player) whoClicked,
                item1, resultEnchants, EnchantingType.ANVIL,
                result.getType() == Material.ENCHANTED_BOOK);

        plugin.getServer().getPluginManager().callEvent(enchantEvent);
        if (enchantEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        Language language = plugin.getLanguagesConfig().getLanguage(whoClicked);
        MaxEnchantCheckResult check = plugin.getEnchantAPI().checkMaxEnchantments(enchantEvent);
        if (check == MaxEnchantCheckResult.TOO_MANY_TOTAL) {
            language.enchant.tooManyTotal.sendTo(enchantEvent.getEnchanter(),
                    plugin.getMainConfig().getGeneralConfig().getAbsoluteMaxEnchants());
            event.setCancelled(true);
        } else if (check == MaxEnchantCheckResult.TOO_MANY_CUSTOM) {
            language.enchant.tooManyCustom.sendTo(enchantEvent.getEnchanter(),
                    plugin.getMainConfig().getGeneralConfig().getAbsoluteMaxEnchants());
            event.setCancelled(true);
        } else if (check == MaxEnchantCheckResult.INVALID_ITEM) {
            event.setCancelled(true);
        }
    }
}
