package net.okocraft.enchantsplus;

import net.okocraft.enchantsplus.listener.NewEnchantListener;

import net.okocraft.enchantsplus.bridge.BridgeManager;
import net.okocraft.enchantsplus.bridge.playerpoints.PlayerPointsBridgeImpl;
import net.okocraft.enchantsplus.bridge.vault.VaultBridgeImpl;
import net.okocraft.enchantsplus.command.Commands;
import net.okocraft.enchantsplus.config.Config;
import net.okocraft.enchantsplus.config.Languages;
import net.okocraft.enchantsplus.config.PlayerData;
import net.okocraft.enchantsplus.config.Tooltips;
import net.okocraft.enchantsplus.enchant.EnchantAPI;
import net.okocraft.enchantsplus.event.MainTimerTickEvent;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.listener.AnvilListener;
import net.okocraft.enchantsplus.listener.GrindstoneListener;
import net.okocraft.enchantsplus.listener.MainListener;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class EnchantsPlus extends JavaPlugin {

    private static EnchantsPlus instance;

    @Getter
    private final Config mainConfig = new Config(this);
    @Getter
    private final Languages languagesConfig = new Languages(this);
    @Getter
    private final PlayerData playerData = new PlayerData(this);
    @Getter
    private final Tooltips tooltipsConfig = new Tooltips(this);
    
    @Getter
    private EnchantAPI enchantAPI = new EnchantAPI(this);
    
    @Getter
    private BridgeManager bridgeManager = new BridgeManager(this);

    @Getter
    private Commands commands;

    @Getter
    private MainListener mainListener;
    
    public void onLoad() {
        instance = this;
        bridgeManager.hookWorldGuardBridge();
    }

    public void onEnable() {
        this.commands = new Commands(this);
        reload();

        new BukkitRunnable(){
            @Override
            public void run() {
                getServer().getPluginManager().callEvent(new MainTimerTickEvent());
                for (Player player : getServer().getOnlinePlayers()) {
                    getServer().getPluginManager().callEvent(new PlayerTickEvent(player));
                }
            }
        }.runTaskTimer(this, 1, 1);

        getLogger().info("Done!");
    }

    public void onDisable() {
    }

    public void reload() {
        reloadConfig();

        bridgeManager.loadBridges();
        getLogger().info("Loaded configs and assigned plugin bridge instances.");
        
        final PluginManager pluginManager = getServer().getPluginManager();
        HandlerList.unregisterAll(this);
        this.mainListener = new MainListener(this);
        pluginManager.registerEvents(mainListener, this);
        pluginManager.registerEvents(new NewEnchantListener(this), this);
        pluginManager.registerEvents(new AnvilListener(this), this);
        pluginManager.registerEvents(new GrindstoneListener(this), this);

        getLogger().info("Registered events.");

        if (bridgeManager.getVaultBridge() instanceof VaultBridgeImpl
                || bridgeManager.getPlayerPointsBridge() instanceof PlayerPointsBridgeImpl) {
            getLogger().info("Detected Vault or PlayerPoints (or both).");
        }
    }

    public LocalItemStack wrapItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }
        return new LocalItemStack(this, item);
    }

    public static EnchantsPlus getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EnchantsPlus is not loaded yet.");
        }
        return instance;
    }

    /**
     * @deprecated Use {@code getMainConfig()}
     */
    @Deprecated
    @Override
    public @NotNull FileConfiguration getConfig() {
        return mainConfig.get();
    }

    @Override
    public void reloadConfig() {
        mainConfig.reload();
        languagesConfig.reload();
        playerData.reload();
        tooltipsConfig.reload();
    }
}
