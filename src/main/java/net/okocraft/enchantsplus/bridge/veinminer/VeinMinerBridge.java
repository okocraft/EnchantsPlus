package net.okocraft.enchantsplus.bridge.veinminer;

import net.okocraft.enchantsplus.bridge.PluginBridgeHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VeinMinerBridge {

    String NAME = "VeinMiner";

    static @NotNull PluginBridgeHolder<VeinMinerBridge> createHolder() {
        return new PluginBridgeHolder<>(
                NAME,
                new VeinMinerBridge() {
                },
                List.of(
                        "wtf.choco.veinminer.VeinMinerPlugin",
                        "wtf.choco.veinminer.player.VeinMinerPlayer",
                        "wtf.choco.veinminer.tool.VeinMinerToolCategory",
                        "wtf.choco.veinminer.tool.VeinMinerToolCategoryHand",
                        "wtf.choco.veinminer.util.VMConstants"
                ),
                VeinMinerBridgeImpl::new
        );
    }

    default boolean isVeinMining(@NotNull Player player) {
        return false;
    }
}
