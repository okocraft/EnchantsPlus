package net.okocraft.enchantsplus.bridge.nocheatplus;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NoCheatPlusBridgeImpl implements NoCheatPlusBridge {

    public NoCheatPlusBridgeImpl() {
        // Test to ensure plugin classes is on runtime.
        fromWrapped(CheckType.BLOCKBREAK);
    }

    @Override
    public void exempt(@NotNull Player player, @NotNull CheckType type) {
        NCPExemptionManager.exemptPermanently(player, fromWrapped(type));
    }

    @Override
    public void unexempt(@NotNull Player player, @NotNull CheckType type) {
        NCPExemptionManager.unexempt(player, fromWrapped(type));
    }

    private static @NotNull fr.neatmonster.nocheatplus.checks.CheckType fromWrapped(@NotNull CheckType type) {
        return fr.neatmonster.nocheatplus.checks.CheckType.valueOf(type.name());
    }
}
