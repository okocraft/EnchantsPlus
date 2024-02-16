package net.okocraft.enchantsplus.bridge.playerpoints;

import net.okocraft.enchantsplus.bridge.PluginBridgeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface PlayerPointsBridge {

    String NAME = "PlayerPoints";

    static @NotNull PluginBridgeHolder<PlayerPointsBridge> createHolder() {
        return new PluginBridgeHolder<>(
                NAME,
                new PlayerPointsBridge() {
                },
                List.of("org.black_ixx.playerpoints.PlayerPoints"),
                PlayerPointsBridgeImpl::new
        );
    }

    default boolean hasPlayerPoints(@NotNull UUID uid, int point) {
        return true;
    }

    default void takePlayerPoints(@NotNull UUID uid, int point) {
    }
}
