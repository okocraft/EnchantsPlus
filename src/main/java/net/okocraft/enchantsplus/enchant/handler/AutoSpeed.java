package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.AutoSpeedConfig;
import net.okocraft.enchantsplus.util.AttributeEditor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AutoSpeed extends AttributeEnchantHandler<AutoSpeedConfig> {

    @Getter
    private final Map<EquipmentSlot, Set<AttributeEditor>> attributeEditors = Map.of(
            EquipmentSlot.FEET,
            Set.of(new AttributeEditor(
                    Attribute.GENERIC_MOVEMENT_SPEED,
                    UUID.fromString("e7c5b784-975e-4d4d-aeb9-0d52f8884518"),
                    "enchantsplus_auto_speed",
                    Operation.MULTIPLY_SCALAR_1
            ))
    );

    public AutoSpeed(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getAutoSpeedConfig());
    }

    @Override
    protected double levelCoefficient() {
        return 0.08;
    }
}
