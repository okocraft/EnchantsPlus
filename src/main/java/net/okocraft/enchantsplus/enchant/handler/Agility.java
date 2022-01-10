package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.AgilityConfig;
import net.okocraft.enchantsplus.util.AttributeEditor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Agility extends AttributeEnchantHandler<AgilityConfig> {

    @Getter
    private final Map<EquipmentSlot, Set<AttributeEditor>> attributeEditors = Map.of(
            EquipmentSlot.HAND,
            Set.of(new AttributeEditor(
                    Attribute.GENERIC_ATTACK_SPEED,
                    UUID.fromString("79089417-6046-4230-8cc9-38692d298d3f"),
                    "enchantsplus_agility",
                    Operation.MULTIPLY_SCALAR_1
            ))
    );

    public Agility(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getAgilityConfig());    
    }

    @Override
    protected double levelCoefficient() {
        return 0.1;
    }
}
