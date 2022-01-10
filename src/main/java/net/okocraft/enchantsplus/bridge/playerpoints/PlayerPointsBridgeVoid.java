package net.okocraft.enchantsplus.bridge.playerpoints;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPointsBridgeVoid implements PlayerPointsBridge {

    @Override
    public boolean hasPlayerPoints(@NotNull UUID uid, int point) {
        return true;
    }

    @Override
    public void takePlayerPoints(@NotNull UUID uid, int point) {
    }
}
