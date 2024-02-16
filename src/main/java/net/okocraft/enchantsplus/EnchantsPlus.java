package net.okocraft.enchantsplus;

import lombok.Getter;
import net.okocraft.enchantsplus.bridge.BridgeManager;
import net.okocraft.enchantsplus.command.Commands;
import net.okocraft.enchantsplus.config.Config;
import net.okocraft.enchantsplus.config.Languages;
import net.okocraft.enchantsplus.config.PlayerData;
import net.okocraft.enchantsplus.config.Tooltips;
import net.okocraft.enchantsplus.enchant.EnchantAPI;
import net.okocraft.enchantsplus.listener.AnvilListener;
import net.okocraft.enchantsplus.listener.GrindstoneListener;
import net.okocraft.enchantsplus.listener.MainListener;
import net.okocraft.enchantsplus.listener.NewEnchantListener;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

public class EnchantsPlus extends JavaPlugin {

    private static Logger logger = NOPLogger.NOP_LOGGER;

    public static @NotNull Logger logger() {
        return EnchantsPlus.logger;
    }

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
    private final EnchantAPI enchantAPI = new EnchantAPI(this);

    @Getter
    private final BridgeManager bridgeManager = new BridgeManager();

    @Getter
    private Commands commands;

    @Getter
    private MainListener mainListener;

    public void onLoad() {
        logger = this.getSLF4JLogger();
        instance = this;

        this.bridgeManager.hookWorldGuardBridge();
    }

    public void onEnable() {
        this.commands = new Commands(this);

        logger.info("Loading available bridges...");
        this.bridgeManager.loadBridges(this);

        reload();
        getLogger().info("Done!");
    }

    public void onDisable() {
    }

    public void reload() {
        reloadConfig();
        getLogger().info("Loaded configs.");
        final PluginManager pluginManager = getServer().getPluginManager();
        HandlerList.unregisterAll(this);
        this.mainListener = new MainListener(this);
        pluginManager.registerEvents(mainListener, this);
        pluginManager.registerEvents(new NewEnchantListener(this), this);
        pluginManager.registerEvents(new AnvilListener(this), this);
        pluginManager.registerEvents(new GrindstoneListener(this), this);

        getLogger().info("Registered events.");
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

    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getVersion() {
        return this.getPluginMeta().getVersion();
    }
}
