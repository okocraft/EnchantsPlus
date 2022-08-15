package net.okocraft.enchantsplus.enchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.event.ItemCustomEnchantEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import net.okocraft.enchantsplus.config.Config;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnchantAPI {

    private static final String[] ROMAN_LEVELS = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX",
            "X" };

    private final EnchantsPlus plugin;

    public static String getLevelSymbol(int level) {
        if (level >= 1 && level <= 10) {
            return ROMAN_LEVELS[level - 1];
        }

        return "enchantment.level." + level;
    }

    public static int fromLevelSymbol(String input) {
        for (int level = 1; level <= 10; ++level) {
            if (ROMAN_LEVELS[level - 1].equals(input)) {
                return level;
            }
        }

        try {
            return Integer.parseInt(input.replaceAll("enchantment.level.", ""));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<EnchantPlus> getEnabledEnchants() {
        List<EnchantPlus> result = new ArrayList<>();
        for (EnchantPlus enchant : EnchantPlus.values()) {
            if (plugin.getMainConfig().getBy(enchant).isEnabled()) {
                result.add(enchant);
            }
        }
        return result;
    }

    public List<EnchantPlus> getDisabledEnchants() {
        List<EnchantPlus> result = new ArrayList<>();
        for (EnchantPlus enchant : EnchantPlus.values()) {
            if (!plugin.getMainConfig().getBy(enchant).isEnabled()) {
                result.add(enchant);
            }
        }
        return result;
    }

    
    public List<EnchantPlus> getPossibleBookEnchantments(LocalItemStack item, Set<Enchantment> enchantsToAdd) {
        List<EnchantPlus> enchants = Collections.synchronizedList(new ArrayList<>());
        if (item.getType() != Material.ENCHANTED_BOOK && item.getType() != Material.BOOK) {
            return enchants;
        }

        for (EnchantPlus enchant : getEnabledEnchants()) {
            for (Enchantment vanilla : enchantsToAdd) {
                if (enchant.getTableTargets().contains(EnchantmentTarget.fromBukkit(vanilla.getItemTarget()))) {
                    enchants.add(enchant);
                }
            }
        }
        return enchants;
    }

    public List<EnchantPlus> getPossibleEnchants(LocalItemStack item) {
        List<EnchantPlus> enchants = new ArrayList<>();

        for (EnchantPlus enchant : getEnabledEnchants()) {
            if (enchant.canEnchant(item.getType())) {
                enchants.add(enchant);
            }
        }

        return enchants;
    }


    public MaxEnchantCheckResult checkMaxEnchantments(ItemCustomEnchantEvent event) {
        LocalItemStack item = event.getItem();
        if (item == null) {
            return MaxEnchantCheckResult.INVALID_ITEM;
        }
        if (event.getIgnoreAbsoluteMaxEnchants()) {
            return MaxEnchantCheckResult.NO_PROBLEM;
        }

        Config.GeneralConfig config = plugin.getMainConfig().getGeneralConfig();
        LocalItemStack clone = item.clone();

        clone.addCustomEnchants(event.getEnchantsToAdd(), false);
        int enchants = clone.getCustomEnchants().size();
        if (config.shouldMaxEnchantsIncludeVanillaEnchants()) {
            enchants += clone.getEnchantments().size();
        }

        if (enchants > config.getAbsoluteMaxEnchants()) {
            if (config.shouldMaxEnchantsIncludeVanillaEnchants()) {
                return MaxEnchantCheckResult.TOO_MANY_TOTAL;
            } else {
                return MaxEnchantCheckResult.TOO_MANY_CUSTOM;
            }
        }

        return MaxEnchantCheckResult.NO_PROBLEM;
    }

    public enum MaxEnchantCheckResult {
        TOO_MANY_TOTAL, TOO_MANY_CUSTOM, NO_PROBLEM, INVALID_ITEM
    }

}
