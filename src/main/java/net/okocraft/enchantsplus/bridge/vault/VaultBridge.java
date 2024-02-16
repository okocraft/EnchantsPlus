package net.okocraft.enchantsplus.bridge.vault;

import net.okocraft.enchantsplus.bridge.BridgeHolder;
import org.jetbrains.annotations.NotNull;

public interface VaultBridge {

    String NAME = "Vault";

    static @NotNull BridgeHolder<VaultBridge> createHolder() {
        return new VaultBridgeHolder();
    }
}
