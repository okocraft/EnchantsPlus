package net.okocraft.enchantsplus.bridge.playerpoints;

import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.jetbrains.annotations.NotNull;

class PlayerPointsBridgeImpl implements PlayerPointsBridge {

    @Override
    public boolean hasPlayerPoints(@NotNull UUID uid, int point) {
        return PlayerPoints.getInstance().getAPI().look(uid) >= point;
    }

    @Override
    public void takePlayerPoints(@NotNull UUID uid, int point) {
        PlayerPoints.getInstance().getAPI().take(uid, point);
    }

}
