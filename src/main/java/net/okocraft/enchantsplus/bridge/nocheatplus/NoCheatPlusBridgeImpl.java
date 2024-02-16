package net.okocraft.enchantsplus.bridge.nocheatplus;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NoCheatPlusBridgeImpl implements NoCheatPlusBridge {

    public NoCheatPlusBridgeImpl() {
        // Test to ensure plugin classes is on runtime.
        CheckType.BLOCKBREAK.getName();
    }

    @Override
    public void exemptBlockBreak(@NotNull Player player) {
        NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);
    }

    @Override
    public void unexemptBlockBreak(@NotNull Player player) {
        NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
    }
}
