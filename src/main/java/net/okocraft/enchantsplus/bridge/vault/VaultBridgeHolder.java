package net.okocraft.enchantsplus.bridge.vault;

import net.milkbowl.vault.economy.Economy;
import net.okocraft.enchantsplus.bridge.BridgeHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

class VaultBridgeHolder implements BridgeHolder<VaultBridge> {

    private VaultBridge bridge = new VaultBridge() {
    };

    @Override
    public @NotNull String getName() {
        return VaultBridge.NAME;
    }

    @Override
    public @NotNull VaultBridge getBridge() {
        return this.bridge;
    }

    @Override
    public boolean loadBridge() {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
        } catch (ClassNotFoundException ignored) {
            return false;
        }

        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (serviceProvider != null) {
            this.bridge = new VaultBridgeImpl(serviceProvider.getProvider());
            return true;
        } else {
            return false;
        }
    }
}
