package net.okocraft.enchantsplus.config;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;

import net.md_5.bungee.api.ChatColor;

public class Tooltips extends CustomConfig {
    
    public Tooltips(EnchantsPlus plugin) {
        super(plugin, "enchant-tooltips.yml");
    }

    private String getPrefix() {
        return get().getString("prefix");
    }

    public String getTooltip(EnchantPlus enchant) {
        return ChatColor.translateAlternateColorCodes('&', getPrefix() + get().getString("tooltips." + enchant.getId()));
    }

}
