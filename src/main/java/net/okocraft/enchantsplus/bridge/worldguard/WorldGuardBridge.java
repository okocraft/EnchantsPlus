package net.okocraft.enchantsplus.bridge.worldguard;

import net.okocraft.enchantsplus.enchant.EnchantPlus;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface WorldGuardBridge {

    boolean canAttack(@NotNull Player player);

    boolean canFly(@NotNull Player player);

    @NotNull @Unmodifiable List<EnchantPlus> getDisabledEnchants(@NotNull LivingEntity entity, @NotNull Location location);

}
