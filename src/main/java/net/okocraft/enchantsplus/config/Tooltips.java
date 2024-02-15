package net.okocraft.enchantsplus.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;

public class Tooltips extends CustomConfig {
    
    public Tooltips(EnchantsPlus plugin) {
        super(plugin, "enchant-tooltips.yml");
    }

    private String getPrefix() {
        return get().getString("prefix");
    }

    public Component getTooltip(EnchantPlus enchant) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(getPrefix() + get().getString("tooltips." + enchant.getId()));
    }

}
