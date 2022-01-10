package net.okocraft.enchantsplus.bridge.nocheatplus;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NoCheatPlusBridge {

    void exempt(@NotNull Player player, @NotNull CheckType type);

    void unexempt(@NotNull Player player, @NotNull CheckType type);
}
