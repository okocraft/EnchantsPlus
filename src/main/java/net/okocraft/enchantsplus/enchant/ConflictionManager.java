package net.okocraft.enchantsplus.enchant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;

import lombok.EqualsAndHashCode;

final class ConflictionManager {

    @EqualsAndHashCode
    private static class WrappedEnchantment {
        private final EnchantPlus custom;
        private final Enchantment bukkit;

        WrappedEnchantment(EnchantPlus custom) {
            this.custom = custom;
            this.bukkit = null;
        }

        WrappedEnchantment(Enchantment bukkit) {
            this.custom = null;
            this.bukkit = bukkit;
        }
    }

    private static final Map<Enchantment, WrappedEnchantment> wrapperMapBukkit = new HashMap<>();
    private static final Map<EnchantPlus, WrappedEnchantment> wrapperMapCustom = new HashMap<>();
    private static final Map<WrappedEnchantment, Set<WrappedEnchantment>> conflictions = new HashMap<>();
    static {
        for (Enchantment enchant : Enchantment.values()) {
            wrapperMapBukkit.put(enchant, new WrappedEnchantment(enchant));
        }
        for (EnchantPlus enchant : EnchantPlus.values()) {
            wrapperMapCustom.put(enchant, new WrappedEnchantment(enchant));
        }

        for (EnchantPlus enchant : EnchantPlus.values()) {
            setConfliction(enchant, enchant);
        }
        setConfliction(EnchantPlus.AUTO_SMELT, Enchantment.SILK_TOUCH);
        setConfliction(EnchantPlus.AUTO_SPEED, EnchantPlus.HEAVY_CURSE);
        setConfliction(EnchantPlus.HUNGER_CURSE, EnchantPlus.WELL_FED);
        setConfliction(EnchantPlus.ICE_ASPECT, Enchantment.FIRE_ASPECT);
        setConfliction(EnchantPlus.SOULBOUND, Enchantment.VANISHING_CURSE);
        setConfliction(EnchantPlus.UNSTABLE_CURSE, EnchantPlus.REJUVINATION);
        setConfliction(EnchantPlus.WATER_BREATHING, Enchantment.OXYGEN);

    }
    
    private static void setConfliction(EnchantPlus enchant, Enchantment... enchants) {
        WrappedEnchantment wrappedEnchant1 = wrapperMapCustom.get(enchant);
        Set<WrappedEnchantment> enchantSet = conflictions.computeIfAbsent(wrappedEnchant1, e -> new HashSet<>());
        
        WrappedEnchantment wrappedEnchant2;
        for (Enchantment enchantBukkit : enchants) {
            wrappedEnchant2 = wrapperMapBukkit.get(enchantBukkit);
            enchantSet.add(wrappedEnchant2);
            conflictions.computeIfAbsent(wrappedEnchant1, e -> new HashSet<>()).add(wrappedEnchant2);
        }
    }

    private static void setConfliction(EnchantPlus enchant, EnchantPlus... enchants) {
        WrappedEnchantment wrappedEnchant1 = wrapperMapCustom.get(enchant);
        Set<WrappedEnchantment> enchantSet = conflictions.computeIfAbsent(wrappedEnchant1, e -> new HashSet<>());
        
        WrappedEnchantment wrappedEnchant2;
        for (EnchantPlus enchantCustom : enchants) {
            wrappedEnchant2 = wrapperMapCustom.get(enchantCustom);
            enchantSet.add(wrappedEnchant2);
            conflictions.computeIfAbsent(wrappedEnchant1, e -> new HashSet<>()).add(wrappedEnchant2);
        }
    }

    static boolean conflictsWith(EnchantPlus enchant1, Enchantment enchant2) {
        return conflictions.get(wrapperMapCustom.get(enchant1)).contains(wrapperMapBukkit.get(enchant2));
    }
    
    static boolean conflictsWith(EnchantPlus enchant1, EnchantPlus enchant2) {
        return conflictions.get(wrapperMapCustom.get(enchant1)).contains(wrapperMapCustom.get(enchant2));
    }
}
