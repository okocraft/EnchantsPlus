package net.okocraft.enchantsplus.bridge.nocheatplus;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NoCheatPlusBridge {

    default void exemptBlockBreak(@NotNull Player player) {
    }

    default void unexemptBlockBreak(@NotNull Player player) {
    }
}
