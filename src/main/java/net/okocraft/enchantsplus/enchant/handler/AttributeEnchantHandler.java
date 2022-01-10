package net.okocraft.enchantsplus.enchant.handler;

import java.util.Map;
import java.util.Set;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.ArmorEnchantConfig;
import net.okocraft.enchantsplus.config.Config.EnchantConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;
import net.okocraft.enchantsplus.util.AttributeEditor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public abstract class AttributeEnchantHandler<C extends EnchantConfig> extends TickDependentEnchantHandler<C> {

    public AttributeEnchantHandler(EnchantsPlus plugin, C config) {
        super(plugin, config);
    }

    protected abstract Map<EquipmentSlot, Set<AttributeEditor>> getAttributeEditors();
    
    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        
        if (!checkTickInterval(player, 20)) {
            return;
        }

        if (!config.isEnabled() || isDisabledByWorldGuard(player, player.getLocation())) {
            removeAttributeModifier(player);
            return;
        }
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Set<AttributeEditor> attributes = getAttributeEditors().get(slot);
            if (attributes == null) {
                continue;
            }

            for (AttributeEditor attribute : attributes) {
                LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
                
                if (item == null || item.getType() == Material.ENCHANTED_BOOK) {
                    attribute.removeAttribute(player);
                    continue;
                }

                if (config instanceof ArmorEnchantConfig armorConfig
                        && !armorConfig.worksInHand()
                        && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                    attribute.removeAttribute(player);
                    continue;
                }
                
                int level = item.getCustomEnchantLevel(config.getType());
                if (level == 0) {
                    attribute.removeAttribute(player);
                } else {
                    double value = levelCoefficient() * level;
                    attribute.updateValue(player, value);
                }
            }

        }
    }

    protected abstract double levelCoefficient();

    public void removeAttributeModifier(Player player) {
        getAttributeEditors().values().stream().flatMap(Set::stream).forEach(
            attribute -> attribute.removeAttribute(player)
        );
    }
}
