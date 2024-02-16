package net.okocraft.enchantsplus.bridge.vault;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

class VaultBridgeImpl implements VaultBridge {

    private final Economy eco;

    VaultBridgeImpl(@NotNull Economy economy) {
        this.eco = economy;
    }
}
