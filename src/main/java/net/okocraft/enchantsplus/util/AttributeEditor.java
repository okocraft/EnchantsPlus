package net.okocraft.enchantsplus.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record AttributeEditor(@NotNull Attribute attribute, @NotNull UUID uuid, @NotNull String name,
                              @NotNull AttributeModifier.Operation operation) {

    public void removeAttribute(@NotNull Player player) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }

        for (AttributeModifier am : attributeInstance.getModifiers()) {
            if (am.getUniqueId().equals(uuid)) {
                attributeInstance.removeModifier(am);
                return;
            }
        }
    }

    public void updateValue(@NotNull Player player, double expectedValue) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }

        for (AttributeModifier am : attributeInstance.getModifiers()) {
            if (am.getUniqueId().equals(uuid)) {
                if (am.getAmount() == expectedValue) {
                    return;
                } else {
                    attributeInstance.removeModifier(am);
                }
            }
        }

        var am = new AttributeModifier(uuid, name, expectedValue, operation);
        attributeInstance.addModifier(am);
    }
}
