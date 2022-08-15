package net.okocraft.enchantsplus.bridge;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.bridge.nocheatplus.NoCheatPlusBridge;
import net.okocraft.enchantsplus.bridge.nocheatplus.NoCheatPlusBridgeImpl;
import net.okocraft.enchantsplus.bridge.nocheatplus.NoCheatPlusBridgeVoid;
import net.okocraft.enchantsplus.bridge.playerpoints.PlayerPointsBridge;
import net.okocraft.enchantsplus.bridge.playerpoints.PlayerPointsBridgeImpl;
import net.okocraft.enchantsplus.bridge.playerpoints.PlayerPointsBridgeVoid;
import net.okocraft.enchantsplus.bridge.vault.VaultBridge;
import net.okocraft.enchantsplus.bridge.vault.VaultBridgeImpl;
import net.okocraft.enchantsplus.bridge.vault.VaultBridgeVoid;
import net.okocraft.enchantsplus.bridge.veinminer.VeinMinerBridge;
import net.okocraft.enchantsplus.bridge.veinminer.VeinMinerBridgeImpl;
import net.okocraft.enchantsplus.bridge.veinminer.VeinMinerBridgeVoid;
import net.okocraft.enchantsplus.bridge.worldguard.WorldGuardBridge;
import net.okocraft.enchantsplus.bridge.worldguard.WorldGuardBridgeImpl;
import net.okocraft.enchantsplus.bridge.worldguard.WorldGuardBridgeVoid;
import net.okocraft.enchantsplus.listener.AdvancedAntiCheatListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;
import java.util.logging.Logger;

public class BridgeManager implements Listener {

    private final EnchantsPlus plugin;

    private final Logger log;
    private final PluginManager pm;

    private NoCheatPlusBridge noCheatPlusBridge;

    private PlayerPointsBridge playerPointsBridge;

    private VaultBridge vaultBridge;

    private VeinMinerBridge veinMinerBridge;

    private WorldGuardBridge worldguardBridge;

    private AdvancedAntiCheatListener advancedAntiCheatListener;


    public BridgeManager(EnchantsPlus plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        this.pm = plugin.getServer().getPluginManager();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("Vault")) {
            this.vaultBridge = newVaultBridge();
        }

        if (event.getPlugin().getName().equals("PlayerPoints")) {
            this.playerPointsBridge = newPlayerPointsBridge();
        }

        if (event.getPlugin().getName().equals("NoCheatPlus")) {
            this.noCheatPlusBridge = newNoCheatPlusBridge();
        }

        if (event.getPlugin().getName().equals("VeinMiner")) {
            this.veinMinerBridge = newVeinMinerBridge();
        }

        registerAdvancedAntiCheatListener();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("WorldGuard")) {
            this.worldguardBridge = new WorldGuardBridgeVoid();
        }

        if (event.getPlugin().getName().equals("Vault")) {
            this.vaultBridge = new VaultBridgeImpl();
        }

        if (event.getPlugin().getName().equals("PlayerPoints")) {
            this.playerPointsBridge = new PlayerPointsBridgeVoid();
        }

        if (event.getPlugin().getName().equals("NoCheatPlus")) {
            this.noCheatPlusBridge = new NoCheatPlusBridgeVoid();
        }

        if (event.getPlugin().getName().equals("VeinMiner")) {
            this.veinMinerBridge = new VeinMinerBridgeVoid();
        }
    }

    public void loadBridges() {
        this.noCheatPlusBridge = newNoCheatPlusBridge();
        this.playerPointsBridge = newPlayerPointsBridge();
        this.vaultBridge = newVaultBridge();
        this.veinMinerBridge = newVeinMinerBridge();

        registerAdvancedAntiCheatListener();
        HandlerList.unregisterAll(this);
        pm.registerEvents(this, plugin);
    }

    public void hookWorldGuardBridge() {
        try {
            this.worldguardBridge = new WorldGuardBridgeImpl();
        } catch (NoClassDefFoundError | IllegalStateException e) {
            log.info("WorldGuard is not available. Ignored.");
            this.worldguardBridge = new WorldGuardBridgeVoid();
        }
    }

    private VaultBridge newVaultBridge() {
        try {
            if (!pm.isPluginEnabled("Vault")) {
                throw new NoClassDefFoundError();
            }
            return new VaultBridgeImpl();
        } catch (NoClassDefFoundError e) {
            log.info("Vault is not installed. Ignored.");
            return new VaultBridgeVoid();
        }
    }

    private PlayerPointsBridge newPlayerPointsBridge() {
        try {
            if (!pm.isPluginEnabled("PlayerPoints")) {
                throw new NoClassDefFoundError();
            }
            return new PlayerPointsBridgeImpl();
        } catch (NoClassDefFoundError e) {
            log.info("PlayerPoints is not installed. Ignored.");
            return new PlayerPointsBridgeVoid();
        }
    }

    private NoCheatPlusBridge newNoCheatPlusBridge() {
        try {
            if (!pm.isPluginEnabled("NoCheatPlus")) {
                throw new NoClassDefFoundError();
            }
            return new NoCheatPlusBridgeImpl();
        } catch (NoClassDefFoundError e) {
            log.info("NoCheatPlus is not installed. Ignored.");
            return new NoCheatPlusBridgeVoid();
        }
    }

    private VeinMinerBridge newVeinMinerBridge() {
        try {
            if (!pm.isPluginEnabled("VeinMiner")) {
                throw new NoClassDefFoundError();
            }
            return new VeinMinerBridgeImpl();
        } catch (NoClassDefFoundError e) {
            log.info("VeinMiner is not installed. Ignored.");
            return new VeinMinerBridgeVoid();
        }
    }

    private void registerAdvancedAntiCheatListener() {
        if (advancedAntiCheatListener != null) {
            HandlerList.unregisterAll(advancedAntiCheatListener);
            advancedAntiCheatListener = null;
        }
        if (pm.isPluginEnabled("AAC")) {
            advancedAntiCheatListener = new AdvancedAntiCheatListener(plugin);
            pm.registerEvents(advancedAntiCheatListener, plugin);
            plugin.getLogger().info("Detected AAC, hooked up into it.");
        }
    }

    public NoCheatPlusBridge getNoCheatPlusBridge() {
        return Objects.requireNonNullElseGet(this.noCheatPlusBridge, NoCheatPlusBridgeVoid::new);
    }

    public PlayerPointsBridge getPlayerPointsBridge() {
        return Objects.requireNonNullElseGet(this.playerPointsBridge, PlayerPointsBridgeVoid::new);
    }

    public VaultBridge getVaultBridge() {
        return Objects.requireNonNullElseGet(this.vaultBridge, VaultBridgeVoid::new);
    }

    public VeinMinerBridge getVeinMinerBridge() {
        return Objects.requireNonNullElseGet(this.veinMinerBridge, VeinMinerBridgeVoid::new);
    }

    public WorldGuardBridge getWorldguardBridge() {
        return Objects.requireNonNullElseGet(this.worldguardBridge, WorldGuardBridgeVoid::new);
    }
}
