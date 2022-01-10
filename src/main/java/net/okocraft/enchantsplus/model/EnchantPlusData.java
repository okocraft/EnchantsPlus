package net.okocraft.enchantsplus.model;

import java.util.Map;

import net.okocraft.enchantsplus.enchant.EnchantPlus;

public class EnchantPlusData {

    final Map<EnchantPlus, Integer> enchantments;
    
    EnchantPlusData(Map<EnchantPlus, Integer> enchantments) {
        this.enchantments = enchantments;
    }
}
