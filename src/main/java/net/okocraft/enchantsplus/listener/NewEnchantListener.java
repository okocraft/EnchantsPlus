package net.okocraft.enchantsplus.listener;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.EnchantingType;
import net.okocraft.enchantsplus.event.ItemCustomEnchantEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewEnchantListener implements Listener {

    private static final int BASE = 3;

    private final Random random = new Random();
    private final Map<EnchantPlus, EnchantSetting> enchantMap = new EnumMap<>(EnchantPlus.class);

    private final EnchantsPlus plugin;

    public NewEnchantListener(@NotNull EnchantsPlus plugin) {
        this.plugin = plugin;

        for (var enchant : EnchantPlus.values()) {
            var enchantConfig = plugin.getMainConfig().getBy(enchant);

            if (enchantConfig.isEnabled() && !enchant.isTreasure()) {
                if (enchant.isCursed() && !plugin.getMainConfig().getGeneralConfig().isCursedEnchantsEnabled()) {
                    continue;
                }

                int chance = Math.max(enchantConfig.getEnchantChance(), 1);
                int maxLevel = Math.max(enchantConfig.getMaxLevel(), 1);
                enchantMap.put(enchant, new EnchantSetting(chance, maxLevel));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        var config = plugin.getMainConfig().getGeneralConfig();
        var enchanter = event.getEnchanter();
        var item = plugin.wrapItem(event.getItem());

        if (item == null ||
                event.getExpLevelCost() < config.getRequiredLevel() ||
                plugin.getPlayerData().isCustomEnchantsDisabled(enchanter.getUniqueId())) {
            return;
        }

        if (random.nextInt(130 - event.getExpLevelCost()) + 1 > config.getEnchantingRate()) {
            return;
        }

        boolean isBook = item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK;
        var vanillaEnchantsToAdd = event.getEnchantsToAdd();
        List<EnchantPlus> enchants;

        if (isBook) {
            enchants = plugin.getEnchantAPI().getPossibleBookEnchantments(item, vanillaEnchantsToAdd.keySet());
        } else {
            enchants = plugin.getEnchantAPI().getPossibleEnchants(item);
        }

        enchants.removeIf(enchant -> !enchantMap.containsKey(enchant) || checkNoEnchantPermission(enchanter, enchant));

        if (enchants.isEmpty()) {
            return;
        }

        int maxEnchants = isBook ? config.getMaxCustomEnchantsOnBook() : config.getMaxCustomEnchants();

        if (maxEnchants <= 0) {
            return;
        }

        int absoluteMaxEnchants = config.getAbsoluteMaxEnchants();
        int vanillaEnchants = vanillaEnchantsToAdd.size();

        if (absoluteMaxEnchants <= vanillaEnchants) {
            return;
        }

        int probability = 1;
        var enchantsToAdd = new HashMap<EnchantPlus, Integer>();

        while (enchantsToAdd.size() < maxEnchants &&
                vanillaEnchants + enchantsToAdd.size() < absoluteMaxEnchants) {
            if (probability != 1 && random.nextInt(probability) != 0) {
                break;
            }

            Collections.shuffle(enchants, random);

            for (EnchantPlus enchant : enchants) {
                if (enchantsToAdd.containsKey(enchant)) {
                    continue;
                }

                if (random.nextInt(100) + 1 <= enchantMap.get(enchant).chance()) {
                    int levelOfEnchantToAdd = random.nextInt(enchantMap.get(enchant).maxLevel()) + 1;
                    enchantsToAdd.put(enchant, levelOfEnchantToAdd);
                    break;
                }
            }

            probability *= BASE;
        }

        if (vanillaEnchantsToAdd.containsKey(Enchantment.SILK_TOUCH)) {
            enchantsToAdd.remove(EnchantPlus.AUTO_SMELT);

            enchantsToAdd.remove(EnchantPlus.REGAIN);
        }

        if (enchantsToAdd.isEmpty()) {
            return;
        }

        ItemCustomEnchantEvent itemCustomEnchantEvent =
                new ItemCustomEnchantEvent(enchanter, item, enchantsToAdd, EnchantingType.ENCHANTING_TABLE, false);

        Bukkit.getPluginManager().callEvent(itemCustomEnchantEvent);

        if (!itemCustomEnchantEvent.isCancelled()) {
            item.addCustomEnchants(itemCustomEnchantEvent.getEnchantsToAdd(), false);
            event.getItem().setItemMeta(item.getItemMeta());
        }
    }

    private boolean checkNoEnchantPermission(Player enchanter, EnchantPlus enchant) {
        return enchanter.hasPermission("enchantsplus.no-enchant." + enchant.getClass().getSimpleName())
                && !enchanter.hasPermission("enchantsplus.admin") && !enchanter.hasPermission("*")
                && !enchanter.hasPermission("enchantsplus.*");
    }

    private record EnchantSetting(int chance, int maxLevel) {
    }
}
