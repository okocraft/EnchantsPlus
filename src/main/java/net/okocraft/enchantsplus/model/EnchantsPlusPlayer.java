package net.okocraft.enchantsplus.model;

import net.okocraft.enchantsplus.event.PlayerTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class EnchantsPlusPlayer {

    private final UUID uuid;
    private long tickCount = 0;

    public EnchantsPlusPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public void tick() {
        Player bukkitPlayer = Bukkit.getPlayer(this.uuid);

        if (bukkitPlayer == null) {
            // Player is already disconnected.
            return;
        }

        new PlayerTickEvent(bukkitPlayer, this, ++this.tickCount).callEvent();
    }
}
