package net.okocraft.enchantsplus.bridge.essentials;

import net.okocraft.enchantsplus.bridge.BridgeHolder;
import net.okocraft.enchantsplus.bridge.PluginBridgeHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EssentialsBridge {

    String NAME = "Essentials";

    static @NotNull BridgeHolder<EssentialsBridge> createHolder() {
        return new PluginBridgeHolder<>(
                NAME,
                new EssentialsBridge() {
                },
                List.of("net.ess3.api.IEssentials"),
                EssentialsBridgeImpl::new
        ) {
            @Override
            public void unloadBridge() {
                this.getBridge().unregisterTabCompletionListener(); // Unregister the listener before unloading this bridge.
                super.unloadBridge();
            }
        };
    }

    default void registerTabCompletionListener(@NotNull Plugin plugin) {
    }

    default void unregisterTabCompletionListener() {
    }

}
