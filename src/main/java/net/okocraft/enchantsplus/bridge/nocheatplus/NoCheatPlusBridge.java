package net.okocraft.enchantsplus.bridge.nocheatplus;

import net.okocraft.enchantsplus.bridge.PluginBridgeHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NoCheatPlusBridge {

    String NAME = "NoCheatPlus";

    static @NotNull PluginBridgeHolder<NoCheatPlusBridge> createHolder() {
        return new PluginBridgeHolder<>(
                NAME,
                new NoCheatPlusBridge() {
                },
                List.of(
                        "fr.neatmonster.nocheatplus.checks.CheckType",
                        "fr.neatmonster.nocheatplus.hooks.NCPExemptionManager"
                ),
                NoCheatPlusBridgeImpl::new
        );
    }

    default void exemptBlockBreak(@NotNull Player player) {
    }

    default void unexemptBlockBreak(@NotNull Player player) {
    }
}
