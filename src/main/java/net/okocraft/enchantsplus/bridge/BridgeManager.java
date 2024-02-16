package net.okocraft.enchantsplus.bridge;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.bridge.nocheatplus.NoCheatPlusBridge;
import net.okocraft.enchantsplus.bridge.playerpoints.PlayerPointsBridge;
import net.okocraft.enchantsplus.bridge.vault.VaultBridge;
import net.okocraft.enchantsplus.bridge.veinminer.VeinMinerBridge;
import net.okocraft.enchantsplus.bridge.worldguard.WorldGuardBridge;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BridgeManager implements Listener {

    // WorldGuard's bridge needs registering flags when the plugin is loading.
    private final BridgeHolder<WorldGuardBridge> worldGuardBridgeHolder = WorldGuardBridge.createHolder();

    // Vault's Economy is an interface, so we cannot detect reloading of an implementation correctly.
    private final BridgeHolder<VaultBridge> vaultBridgeHolder = VaultBridge.createHolder();

    // These bridges can be loaded/unloaded while the server is running. Currently, PluginEnableEvent and PluginDisableEvent do so.
    private final Map<String, BridgeHolder<?>> bridgeMap = Map.of(
            NoCheatPlusBridge.NAME, NoCheatPlusBridge.createHolder(),
            PlayerPointsBridge.NAME, PlayerPointsBridge.createHolder(),
            VeinMinerBridge.NAME, VeinMinerBridge.createHolder()
    );

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (this.bridgeMap.get(event.getPlugin().getName()) instanceof PluginBridgeHolder<?> holder) {
            this.loadBridge(holder);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (this.bridgeMap.get(event.getPlugin().getName()) instanceof PluginBridgeHolder<?> holder) {
            holder.unloadBridge();
            EnchantsPlus.logger().info("A bridge for '{}' is unloaded due to the plugin being disabled.", holder.getName());
        }
    }

    public void loadBridges(@NotNull EnchantsPlus plugin) {
        this.loadBridge(this.vaultBridgeHolder);
        this.bridgeMap.values().forEach(this::loadBridge);

        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void hookWorldGuardBridge() {
        this.loadBridge(this.worldGuardBridgeHolder);
    }

    public NoCheatPlusBridge getNoCheatPlusBridge() {
        return this.getBridge(NoCheatPlusBridge.NAME, NoCheatPlusBridge.class);
    }

    public PlayerPointsBridge getPlayerPointsBridge() {
        return this.getBridge(PlayerPointsBridge.NAME, PlayerPointsBridge.class);
    }

    public VaultBridge getVaultBridge() {
        return this.vaultBridgeHolder.getBridge();
    }

    public VeinMinerBridge getVeinMinerBridge() {
        return this.getBridge(VeinMinerBridge.NAME, VeinMinerBridge.class);
    }

    public WorldGuardBridge getWorldguardBridge() {
        return this.worldGuardBridgeHolder.getBridge();
    }

    private <B> @NotNull B getBridge(@NotNull String name, @NotNull Class<B> clazz) {
        return clazz.cast(this.bridgeMap.get(name).getBridge());
    }

    private void loadBridge(@NotNull BridgeHolder<?> holder) {
        if (holder.loadBridge()) {
            EnchantsPlus.logger().info("A bridge for {} is loaded.", holder.getName());
        }
    }
}
