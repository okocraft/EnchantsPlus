package net.okocraft.enchantsplus.enchant.handler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.LifeConfig;
import net.okocraft.enchantsplus.util.AttributeEditor;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

import lombok.Getter;

public class Life extends AttributeEnchantHandler<LifeConfig> {
    
    @Getter
    private final Map<EquipmentSlot, Set<AttributeEditor>> attributeEditors = Map.of(
            EquipmentSlot.HAND,
            createAttributeSet(UUID.fromString("5c4295de-4705-4d32-8df7-71c5d690922e")),
            EquipmentSlot.OFF_HAND,
            createAttributeSet(UUID.fromString("dbbabd58-7bf5-4e63-ad6d-f524a353874a")),
            EquipmentSlot.HEAD,
            createAttributeSet(UUID.fromString("6ae3ebbb-5e14-4e17-a422-2ba761212040")),
            EquipmentSlot.CHEST,
            createAttributeSet(UUID.fromString("5d65cbd3-987b-4f43-9ec7-02976dd6c385")),
            EquipmentSlot.LEGS,
            createAttributeSet(UUID.fromString("d4a7ef65-a218-4edd-934a-ce451c87488d")),
            EquipmentSlot.FEET,
            createAttributeSet(UUID.fromString("6a731258-b416-4a04-9005-2ae764b5cf0e"))
    );

    public Life(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getLifeConfig());
    }

    private static Set<AttributeEditor> createAttributeSet(UUID uuid) {
        return Set.of(new AttributeEditor(
                Attribute.GENERIC_MAX_HEALTH,
                uuid,
                "enchantsplus_life",
                Operation.ADD_NUMBER
        ));
    }

    @Override
    protected double levelCoefficient() {
        return config.getHealthPerLevel();
    }
}
