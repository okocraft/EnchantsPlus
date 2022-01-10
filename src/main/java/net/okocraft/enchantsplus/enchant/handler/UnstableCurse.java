package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.UnstableCurseConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class UnstableCurse extends TickDependentEnchantHandler<UnstableCurseConfig> {

    public UnstableCurse(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getUnstableCurseConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!checkTickInterval(player, 20)) {
            return;
        }

        if (!config.isEnabled()) {
            return;
        }

        if (isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
            if (!isValidTool(item) || item.getItemMeta().isUnbreakable()) {
                continue;
            }

            int level = item.getCustomEnchantLevel(EnchantPlus.UNSTABLE_CURSE);
            if (random.nextDouble() < config.getDecayRate(level)) {
                callItemDamageEvent(player, item.getItem(), 1);
            }
        }
    }
}
