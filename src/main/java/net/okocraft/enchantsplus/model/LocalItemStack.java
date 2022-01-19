package net.okocraft.enchantsplus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.EnchantConfig;
import net.okocraft.enchantsplus.enchant.EnchantAPI;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.util.NamespacedKeyManager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import net.md_5.bungee.api.ChatColor;

public class LocalItemStack {

    private final EnchantsPlus plugin;

    private final ItemStack handle;

    private final EnchantPlusData enchantPlusData;

    private final List<String> lore = new ArrayList<>();

    public LocalItemStack(EnchantsPlus plugin, ItemStack handle) {
        this.plugin = plugin;
        this.handle = handle.clone();

        PersistentDataContainer container = getHandledMeta().getPersistentDataContainer();
        this.enchantPlusData = container.getOrDefault(
                NamespacedKeyManager.ENCHANTMENTS_KEY,
                CustomDataTypes.ENCHANT_PLUS_DATA,
                new EnchantPlusData(new HashMap<>())
        );
        if (!container.has(NamespacedKeyManager.LORE_KEY, CustomDataTypes.STRING_ARRAY)) {
            List<String> originalLore = getHandledMeta().getLore();
            setLore(originalLore);
        } else {
            setLore(new ArrayList<>(Arrays.asList(
                container.get(NamespacedKeyManager.LORE_KEY, CustomDataTypes.STRING_ARRAY)
            )));
        }
    }

    public ItemStack getItem() {
        ItemStack item = handle.clone();
        if (!getCustomEnchants().isEmpty()) {
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.remove(NamespacedKeyManager.ENCHANTMENTS_KEY);
        container.remove(NamespacedKeyManager.LORE_KEY);
        item.setItemMeta(meta);
        return item;
    }

    public void setItemMeta(ItemMeta itemMeta) {
        handle.setItemMeta(itemMeta);
    }

    public Material getType() {
        return handle.getType();
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return handle.getEnchantments();
    }

    public int getAmount() {
        return handle.getAmount();
    }

    public void setAmount(int amount) {
        handle.setAmount(amount);
    }

    public int getEnchantmentLevel(Enchantment ench) {
        return handle.getEnchantmentLevel(ench);
    }

    public void addEnchantment(Enchantment ench, int level) {
        handle.addEnchantment(ench, level);
    }

    public void setLore(List<String> lore) {
        this.lore.clear();
        if (lore != null) {
            this.lore.addAll(lore);
        } 

        ItemMeta meta = getHandledMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(NamespacedKeyManager.LORE_KEY, CustomDataTypes.STRING_ARRAY, this.lore.toArray(String[]::new));
        setItemMeta(meta);

        fixLore();
    }

    public boolean hasLore() {
        return !lore.isEmpty();
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    private void fixLore() {
        ItemMeta meta = getHandledMeta();
        List<String> realLore = new ArrayList<>();

        List<EnchantPlus> enchants = new ArrayList<>(enchantPlusData.enchantments.keySet());
        if (!enchants.isEmpty()) {
            Collections.sort(enchants, (e1, e2) -> e1.getId().compareTo(e2.getId()));
            List<String> tooltips = new ArrayList<>();

            for (EnchantPlus enchant : enchants) {
                int level = enchantPlusData.enchantments.get(enchant);

                String enchantDisplayName = plugin.getMainConfig().getBy(enchant).getDisplayName();
                realLore.add((enchant.isCursed() ? ChatColor.RED : ChatColor.GRAY) + enchantDisplayName + " "
                        + EnchantAPI.getLevelSymbol(level));
                tooltips.add(plugin.getTooltipsConfig().getTooltip(enchant));
            }

            if (plugin.getMainConfig().getGeneralConfig().isHelpfulTooltipsEnabled()) {
                realLore.add("");
                realLore.addAll(tooltips);
            }
        }

        if (hasLore()) {
            realLore.addAll(lore);
        }
        meta.setLore(realLore);
        setItemMeta(meta);
    }

    public List<EnchantPlus> getPossibleCustomEnchants(ItemStack item) {
        List<EnchantPlus> enchants = new ArrayList<>();

        for (EnchantPlus enchant : EnchantPlus.values()) {
            if (enchant.canEnchant(getType())) {
                enchants.add(enchant);
            }
        }

        return enchants;
    }

    public Map<EnchantPlus, Integer> getCustomEnchants() {
        return new HashMap<>(enchantPlusData.enchantments);
    }

    public int getCustomEnchantLevel(EnchantPlus enchant) {
        return enchantPlusData.enchantments.getOrDefault(enchant, 0);
    }

    public boolean hasCustomEnchant(EnchantPlus enchant) {
        return enchantPlusData.enchantments.containsKey(enchant);
    }

    public boolean hasCustomEnchants() {
        return !enchantPlusData.enchantments.isEmpty();
    }

    public int removeCustomEnchant(EnchantPlus enchant) {
        Integer previousLevel = enchantPlusData.enchantments.remove(enchant);
        previousLevel = previousLevel == null ? 0 : previousLevel;
        saveEnchantPlusData();
        fixLore();
        return previousLevel;
    }

    public boolean addCustomEnchant(EnchantPlus enchant, int level, boolean ignoreLevelRestriction, boolean ignoreConfliction) {
        level = ignoreLevelRestriction ? level
                : Math.min(level, plugin.getMainConfig().getBy(enchant).getMaxLevel());
        if (!ignoreConfliction && enchant.conflictsWith(getCustomEnchants().keySet(), getEnchantments().keySet())) {
            return false;
        }
        if (level == 0) {
            return removeCustomEnchant(enchant) != 0;
        }
        Integer previousLevel = enchantPlusData.enchantments.put(enchant, level);
        if (previousLevel == null) {
            previousLevel = 0;
        }
        if (previousLevel != level) {
            saveEnchantPlusData();
            fixLore();
            return true;
        } else {
            return false;
        }
    }

    public void addCustomEnchants(Map<EnchantPlus, Integer> enchants, boolean ignoreLevelRestriction) {
        for (Map.Entry<EnchantPlus, Integer> entry : enchants.entrySet()) {
            int level = ignoreLevelRestriction ? entry.getValue()
                    : Math.min(entry.getValue(), plugin.getMainConfig().getBy(entry.getKey()).getMaxLevel());
            enchantPlusData.enchantments.put(entry.getKey(), level);
        }
        saveEnchantPlusData();
        fixLore();
    }

    public void combineCustomEnchants(Map<EnchantPlus, Integer> enchants) {
        EnchantConfig config;
        for (Map.Entry<EnchantPlus, Integer> entry : enchants.entrySet()) {
            if (!entry.getKey().canEnchantWithAnvil(getType())) {
                continue;
            }
            if (entry.getKey().conflictsWith(enchantPlusData.enchantments.keySet(), getEnchantments().keySet())) {
                continue;
            }
            config = plugin.getMainConfig().getBy(entry.getKey());
            if (config.isAnvilDisabled()) {
                continue;
            }
            int currentLevel = enchantPlusData.enchantments.getOrDefault(entry.getKey(), 0);
            int level;
            if (currentLevel == entry.getValue() && !config.isLevelConbiningDisabled()) {
                level = currentLevel + 1;
            } else {
                level = Math.max(currentLevel, entry.getValue());
            }
            level = Math.min(level, config.getMaxLevel());
            enchantPlusData.enchantments.put(entry.getKey(), level);
        }
        saveEnchantPlusData();
        fixLore();
    }

    public void clearCustomEnchant() {
        if (!enchantPlusData.enchantments.isEmpty()) {
            enchantPlusData.enchantments.clear();
            saveEnchantPlusData();
            fixLore();
        }
    }

    public void useGrindstone() {
        if (!enchantPlusData.enchantments.isEmpty()) {
            enchantPlusData.enchantments.keySet().removeIf(
                    enchant -> !plugin.getMainConfig().getBy(enchant).isGrindstoneDisabled()
            );
            saveEnchantPlusData();
            fixLore();
        }
    }

    private void saveEnchantPlusData() {
        ItemMeta meta = getHandledMeta();
        if (enchantPlusData.enchantments.isEmpty()) {
            meta.getPersistentDataContainer().remove(NamespacedKeyManager.ENCHANTMENTS_KEY);
        } else {
            meta.getPersistentDataContainer().set(NamespacedKeyManager.ENCHANTMENTS_KEY,
                    CustomDataTypes.ENCHANT_PLUS_DATA, enchantPlusData);
        }
        setItemMeta(meta);
    }

    private ItemMeta getHandledMeta() {
        return Objects.requireNonNull(handle.getItemMeta(), "Item meta of LocalItemStack cannot be null. It's bug.");
    }

    public ItemMeta getItemMeta() {
        return Objects.requireNonNull(getItem().getItemMeta(), "Item meta of LocalItemStack cannot be null. It's bug.");
    }

    @Override
    public LocalItemStack clone() {
        return plugin.wrapItem(handle.clone());
    }

}
