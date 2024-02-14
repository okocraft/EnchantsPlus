package net.okocraft.enchantsplus.bridge.veinminer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wtf.choco.veinminer.VeinMinerPlugin;
import wtf.choco.veinminer.player.VeinMinerPlayer;
import wtf.choco.veinminer.tool.VeinMinerToolCategory;
import wtf.choco.veinminer.tool.VeinMinerToolCategoryHand;
import wtf.choco.veinminer.util.VMConstants;

public class VeinMinerBridgeImpl implements VeinMinerBridge {

    private final VeinMinerPlugin plugin;

    public VeinMinerBridgeImpl() {
        this.plugin = JavaPlugin.getPlugin(VeinMinerPlugin.class);
    }

    @Override
    public boolean isVeinMining(@NotNull Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();
        VeinMinerToolCategory category = this.plugin.getToolCategoryRegistry().get(item, cat -> player.hasPermission(VMConstants.PERMISSION_VEINMINE.apply(cat)));

        if (!(category instanceof VeinMinerToolCategoryHand)) {
            return false;
        }

        VeinMinerPlayer veinMinerPlayer = this.plugin.getPlayerManager().get(player);

        if (veinMinerPlayer == null) {
            return false;
        }

        return veinMinerPlayer.getActivationStrategy().isActive(veinMinerPlayer) &&
                !this.plugin.getConfiguration().isDisabledGameMode(player.getGameMode()) &&
                !veinMinerPlayer.isVeinMinerEnabled(category) &&
                category.getConfiguration().isDisabledWorld(player.getWorld());
    }
}
