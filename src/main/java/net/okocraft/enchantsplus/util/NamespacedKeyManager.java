package net.okocraft.enchantsplus.util;

import net.okocraft.enchantsplus.EnchantsPlus;

import org.bukkit.NamespacedKey;

/**
 * Key variable name is same as key name.
 * For example SOMEKEY_KEY is "somekey"
 * No underscore before _KEY.
 */
public class NamespacedKeyManager {
    private static final EnchantsPlus PLUGIN = EnchantsPlus.getInstance();

    // used in LocalItemStack
    public static final NamespacedKey ENCHANTMENTS_KEY = new NamespacedKey(PLUGIN, "enchantments");
    public static final NamespacedKey LORE_KEY = new NamespacedKey(PLUGIN, "lore");
    public static final NamespacedKey ENCHANT_LORE_KEY = new NamespacedKey(PLUGIN, "enchant_lore");
    
    // used in CustomDataTypes
    public static final NamespacedKey ID_KEY = new NamespacedKey(PLUGIN, "id");
    public static final NamespacedKey LEVEL_KEY = new NamespacedKey(PLUGIN, "level");
    public static final NamespacedKey TEXT_KEY = new NamespacedKey(PLUGIN, "text");
    
    // used in Harvest
    public static final NamespacedKey HARVESTING_ORIGINAL_FORTUNE = new NamespacedKey(PLUGIN, "original_fortune");
    public static final NamespacedKey HARVESTING_PREVIOUS_LEVEL = new NamespacedKey(PLUGIN, "harvesting_previous_level");
}
