package net.okocraft.enchantsplus.bridge.nocheatplus;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class NoCheatPlusBridgeImpl implements NoCheatPlusBridge {

    @Override
    public void exemptBlockBreak(@NotNull Player player) {
        NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);
    }

    @Override
    public void unexemptBlockBreak(@NotNull Player player) {
        NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
    }
}
