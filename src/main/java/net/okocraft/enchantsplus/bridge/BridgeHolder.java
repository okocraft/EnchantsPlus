package net.okocraft.enchantsplus.bridge;

import org.jetbrains.annotations.NotNull;

public interface BridgeHolder<B> {

    @NotNull String getName();

    @NotNull B getBridge();

    boolean loadBridge();

}
