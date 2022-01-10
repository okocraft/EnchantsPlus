package net.okocraft.enchantsplus.bridge.vault;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.Nullable;

public interface VaultBridge {
    @Nullable Economy getEco();
}
