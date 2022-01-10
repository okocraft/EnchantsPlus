package net.okocraft.enchantsplus.bridge.worldguard;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WorldGuardBridgeImpl implements WorldGuardBridge {

    @Getter
    private final StringFlag disabledCustomEnchantsFlag;

    public WorldGuardBridgeImpl() {
        disabledCustomEnchantsFlag = registerFlag(new StringFlag("disabled-custom-enchants"));
    }

    @SuppressWarnings("unchecked")
    private <F extends Flag<?>> F registerFlag(@NotNull F flag) {
        var registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            registry.register(flag);
            return flag;
        } catch (FlagConflictException | IllegalStateException e) {
            var registeredFlag = registry.get(flag.getName());

            if (flag.getClass().isInstance(registeredFlag)) { // registeredFlag instanceof F
                return (F) registeredFlag;
            } else {
                var logger = EnchantsPlus.getInstance().getLogger();
                logger.warning("Could not register the flag '" + flag.getName() + "'.");
                logger.warning("Please restart your server to register the new flag to WorldGuard");
                throw new IllegalStateException("Cannot register flag " + flag.getName(), e);
            }
        }
    }

    private @Nullable ApplicableRegionSet createRegionSet(@NotNull Location location) {
        if (location.getWorld() == null) {
            return null;
        }

        return createRegionSet(BukkitAdapter.adapt(location.getWorld()), BukkitAdapter.adapt(location));
    }

    private @Nullable ApplicableRegionSet createRegionSet(@NotNull World world,
                                                          @NotNull com.sk89q.worldedit.util.Location location) {
        var regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);

        if (regionManager == null) {
            return null;
        }

        return new RegionResultSet(
                regionManager.getApplicableRegions(location.toVector().toBlockPoint()).getRegions(),
                regionManager.getRegion(GlobalProtectedRegion.GLOBAL_REGION)
        );
    }

    @Override
    public boolean canAttack(@NotNull Player player) {
        var wrappedPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        var regionSet = createRegionSet(wrappedPlayer.getWorld(), wrappedPlayer.getLocation());

        return regionSet == null || regionSet.queryState(wrappedPlayer, Flags.PVP) != State.DENY;
    }

    @Override
    public boolean canFly(@NotNull Player player) {
        return getDisabledEnchants(player, player.getLocation()).contains(EnchantPlus.UPDRAFT);
    }

    @Override
    public @NotNull @Unmodifiable List<EnchantPlus> getDisabledEnchants(@NotNull LivingEntity entity,
                                                                        @NotNull Location location) {
        if (disabledCustomEnchantsFlag == null) {
            return Collections.emptyList();
        }

        var regionSet = createRegionSet(location);

        if (regionSet == null) {
            return Collections.emptyList();
        }

        var associable = entity instanceof Player player ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
        var enchantsStr = regionSet.queryValue(associable, disabledCustomEnchantsFlag);

        if (enchantsStr != null) {
            return Arrays.stream(enchantsStr.split(" "))
                    .map(EnchantPlus::fromId)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}
