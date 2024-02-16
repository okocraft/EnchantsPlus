package net.okocraft.enchantsplus.bridge.worldguard;

import net.okocraft.enchantsplus.bridge.BridgeHolder;
import org.jetbrains.annotations.NotNull;

class WorldGuardBridgeHolder implements BridgeHolder<WorldGuardBridge> {

    private WorldGuardBridge bridge = new WorldGuardBridgeVoid();

    @Override
    public @NotNull String getName() {
        return WorldGuardBridge.NAME;
    }

    @Override
    public @NotNull WorldGuardBridge getBridge() {
        return this.bridge;
    }

    @Override
    public boolean loadBridge() {
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
        } catch (ClassNotFoundException ignored) {
            return false;
        }

        this.bridge = new WorldGuardBridgeImpl();
        return true;
    }
}
