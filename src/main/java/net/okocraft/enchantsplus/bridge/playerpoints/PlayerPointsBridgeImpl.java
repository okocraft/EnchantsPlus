package net.okocraft.enchantsplus.bridge.playerpoints;

import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.jetbrains.annotations.NotNull;

public class PlayerPointsBridgeImpl implements PlayerPointsBridge {

    public PlayerPointsBridgeImpl() {
        // Test to ensure plugin classes is on runtime.
        hasPlayerPoints(UUID.randomUUID(), 1);
    }

    @Override
    public boolean hasPlayerPoints(@NotNull UUID uid, int point) {
        return PlayerPoints.getInstance().getAPI().look(uid) >= point;
    }

    @Override
    public void takePlayerPoints(@NotNull UUID uid, int point) {
        PlayerPoints.getInstance().getAPI().take(uid, point);
    }
}
