package net.okocraft.enchantsplus.bridge.worldguard;

import java.util.Collections;
import java.util.List;

import net.okocraft.enchantsplus.enchant.EnchantPlus;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

class WorldGuardBridgeVoid implements WorldGuardBridge {

    @Override
    public boolean canAttack(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canFly(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull @Unmodifiable List<EnchantPlus> getDisabledEnchants(@NotNull LivingEntity entity,
                                                                        @NotNull Location location) {
        return Collections.emptyList();
    }
}
