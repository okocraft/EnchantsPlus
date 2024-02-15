package net.okocraft.enchantsplus.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.EnchantConfig;
import net.okocraft.enchantsplus.enchant.EnchantAPI;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.util.NamespacedKeyManager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalItemStack {

    private final EnchantsPlus plugin;

    private final ItemStack handle;

    private final EnchantPlusData enchantPlusData;

    public LocalItemStack(EnchantsPlus plugin, ItemStack handle) {
        this.plugin = plugin;
        this.handle = handle.clone();

        ItemMeta meta = handle.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        this.enchantPlusData = container.getOrDefault(
                NamespacedKeyManager.ENCHANTMENTS_KEY,
                CustomDataTypes.ENCHANT_PLUS_DATA,
                new EnchantPlusData(new HashMap<>())
        );
        if (!container.has(NamespacedKeyManager.LORE_KEY, PersistentDataType.LIST.strings()) ||
                !container.has(NamespacedKeyManager.ENCHANT_LORE_KEY, PersistentDataType.LIST.strings())) {
            this.setOriginalLore(meta, handle.lore());
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
        container.remove(NamespacedKeyManager.ENCHANT_LORE_KEY);
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

    private void setOriginalLore(@NotNull ItemMeta meta, @Nullable List<Component> originalLore) {
        setLoreData(meta, NamespacedKeyManager.LORE_KEY, toJson(originalLore));

        List<Component> enchantLore = this.createEnchantLore();
        setLoreData(meta, NamespacedKeyManager.ENCHANT_LORE_KEY, toJson(enchantLore));

        List<Component> newLore;

        if (originalLore != null) {
            newLore = new ArrayList<>(enchantLore.size() + originalLore.size());
            newLore.addAll(enchantLore);
            newLore.addAll(originalLore);
        } else {
            newLore = enchantLore;
        }

        meta.lore(newLore);
        this.setItemMeta(meta);
    }

    public int calculateOriginalLoreLines() {
        return getLoreData(this.getHandledMeta(), NamespacedKeyManager.LORE_KEY).size();
    }

    public int calculateEnchantLoreLines() {
        return getLoreData(this.getHandledMeta(), NamespacedKeyManager.ENCHANT_LORE_KEY).size();
    }

    public void fixLore() {
        ItemMeta meta = this.getHandledMeta();

        List<Component> savedLore;
        int enchantLineSize;

        {
            List<String> enchantLoreData = getLoreData(meta, NamespacedKeyManager.ENCHANT_LORE_KEY);
            List<String> originalLoreData = getLoreData(meta, NamespacedKeyManager.LORE_KEY);

            savedLore = new ArrayList<>(enchantLoreData.size() + originalLoreData.size());
            savedLore.addAll(fromJson(enchantLoreData));
            savedLore.addAll(fromJson(originalLoreData));

            enchantLineSize = enchantLoreData.size();
        }

        List<Component> realLore;

        {
            List<Component> lore = meta.lore();
            realLore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        }

        // Add operation.
        boolean isAddOperation = true;
        for (int i = 0; i < savedLore.size(); i++) {
            if (realLore.size() <= i) {
                isAddOperation = false;
                break;
            }
            Component savedLine = savedLore.get(i);
            Component realLine = realLore.get(i);
            if (!savedLine.equals(realLine)) {
                isAddOperation = false;
                break;
            }
        }

        if (isAddOperation) {
            if (savedLore.isEmpty()) {
                this.setOriginalLore(meta, Collections.emptyList());
            } else {
                savedLore.addAll(realLore.subList(savedLore.size(), realLore.size()));
                this.setOriginalLore(meta, savedLore.subList(enchantLineSize, savedLore.size()));
            }
            return;
        }

        // Set or remove operation.
        while (realLore.size() < savedLore.size()) {
            realLore.add(null);
        }
        while (savedLore.size() < realLore.size() + enchantLineSize) {
            savedLore.add(null);
        }

        for (int i = realLore.size() - 1; i >= 0; i--) {
            Component savedLine = savedLore.get(i);
            Component realLine = realLore.get(i);

            if (!Objects.equals(savedLine, realLine)) {
                savedLore.set(i + enchantLineSize, realLine);
            }
        }

        ListIterator<Component> originalLoreIt = savedLore.listIterator(savedLore.size());
        while (originalLoreIt.hasPrevious()) {
            if (originalLoreIt.previous() == null) {
                originalLoreIt.remove();
            } else {
                break;
            }
        }

        savedLore.replaceAll(line -> line == null ? Component.empty() : line);
        this.setOriginalLore(meta, savedLore.subList(enchantLineSize, savedLore.size()));
    }

    private List<Component> createEnchantLore() {
        if (this.enchantPlusData.enchantments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map.Entry<EnchantPlus, Integer>> entries = new ArrayList<>(this.enchantPlusData.enchantments.entrySet());
        entries.sort(Comparator.comparing(entry -> entry.getKey().getId()));

        boolean tooltipEnabled = this.plugin.getMainConfig().getGeneralConfig().isHelpfulTooltipsEnabled();
        int size = entries.size();
        List<Component> enchantLore = new ArrayList<>(tooltipEnabled ? size + 1 + size : size); // enchants + empty line + tooltips or enchants only
        List<Component> toolTips = tooltipEnabled ? new ArrayList<>(size) : Collections.emptyList();

        for (Map.Entry<EnchantPlus, Integer> entry : entries) {
            EnchantPlus enchant = entry.getKey();
            int level = entry.getValue();
            Component component = plugin.getMainConfig().getBy(enchant).displayName();
            TextColor color = enchant.isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY;

            enchantLore.add(Component.text().append(component, Component.space(), Component.text(EnchantAPI.getLevelSymbol(level)).color(color)).build());

            if (tooltipEnabled) {
                toolTips.add(this.plugin.getTooltipsConfig().getTooltip(enchant));
            }
        }

        if (tooltipEnabled) {
            enchantLore.add(Component.empty());
            enchantLore.addAll(toolTips);
        }

        return enchantLore;
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
        var set = new HashSet<EnchantPlus>();
        for (Map.Entry<EnchantPlus, Integer> entry : enchants.entrySet()) {
            if (!entry.getKey().canEnchantWithAnvil(getType())) {
                continue;
            }
            set.clear();
            set.addAll(enchantPlusData.enchantments.keySet());
            set.remove(entry.getKey());
            if (entry.getKey().conflictsWith(set, getEnchantments().keySet())) {
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

    private static @NotNull List<String> getLoreData(@NotNull ItemMeta meta, @NotNull NamespacedKey key) {
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.LIST.strings(), Collections.emptyList());
    }

    private static void setLoreData(@NotNull ItemMeta meta, @NotNull NamespacedKey key, @NotNull List<String> loreData) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.LIST.strings(), loreData);
    }

    private static @NotNull List<String> toJson(@Nullable Collection<Component> components) {
        if (components == null || components.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(components.size());

        for (Component component : components) {
            result.add(GsonComponentSerializer.gson().serialize(component));
        }

        return result;
    }

    private static @NotNull List<Component> fromJson(@NotNull List<String> jsons) {
        if (jsons.isEmpty()) {
            return Collections.emptyList();
        }

        List<Component> result = new ArrayList<>(jsons.size());

        for (String json : jsons) {
            result.add(GsonComponentSerializer.gson().deserialize(json));
        }

        return result;
    }

}
