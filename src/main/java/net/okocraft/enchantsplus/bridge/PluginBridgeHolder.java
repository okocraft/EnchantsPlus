package net.okocraft.enchantsplus.bridge;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

public class PluginBridgeHolder<B> implements BridgeHolder<B> {

    private final String name;
    private final B defaultImplementation;
    private final Collection<String> requiredClasses;
    private final Supplier<? extends B> implementationSupplier;
    private @NotNull B loadedBridge;

    public PluginBridgeHolder(@NotNull String name, @NotNull B defaultImplementation, @NotNull Collection<String> requiredClasses,
                              @NotNull Supplier<? extends B> implementationSupplier) {
        this.name = name;
        this.defaultImplementation = defaultImplementation;
        this.requiredClasses = requiredClasses;
        this.implementationSupplier = implementationSupplier;
        this.loadedBridge = this.defaultImplementation;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull B getBridge() {
        return this.loadedBridge;
    }

    @Override
    public boolean loadBridge() {
        if (!Bukkit.getPluginManager().isPluginEnabled(this.name)) {
            return false;
        }

        for (var requiredClass : this.requiredClasses) {
            try {
                Class.forName(requiredClass);
            } catch (ClassNotFoundException ignored) {
                return false;
            }
        }

        var bridge = this.implementationSupplier.get();
        if (bridge != null) {
            this.loadedBridge = bridge;
            return true;
        } else {
            return false;
        }
    }

    public void unloadBridge() {
        this.loadedBridge = this.defaultImplementation;
    }
}
