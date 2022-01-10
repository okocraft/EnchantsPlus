package net.okocraft.enchantsplus.bridge.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class VaultBridgeImpl implements VaultBridge {

    @Getter
    private @NotNull Economy eco;

    public VaultBridgeImpl() {
        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (serviceProvider == null) {
            throw new NoClassDefFoundError("Vault is not installed.");
        }
        this.eco = serviceProvider.getProvider();
    }
}
