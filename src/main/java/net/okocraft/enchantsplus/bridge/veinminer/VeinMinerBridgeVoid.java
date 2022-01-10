package net.okocraft.enchantsplus.bridge.veinminer;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VeinMinerBridgeVoid implements VeinMinerBridge {

    @Override
    public boolean isVeinMining(@NotNull Player player) {
        return false;
    }
}
