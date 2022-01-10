package net.okocraft.enchantsplus.bridge.playerpoints;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerPointsBridge {

    boolean hasPlayerPoints(@NotNull UUID uid, int point);

    void takePlayerPoints(@NotNull UUID uid, int point);
}
