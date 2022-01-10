package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.WellFedConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.EquipmentSlot;

public class WellFed extends TickDependentEnchantHandler<WellFedConfig> {

    public WellFed(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getWellFedConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        if (!config.isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!checkTickInterval(player, 20)) {
            return;
        }
        if (isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }
        int totalLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!config.worksInHand() && (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND)) {
                continue;
            }
            LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
            if (!isValidTool(item)) {
                continue;
            }

            totalLevel += item.getCustomEnchantLevel(EnchantPlus.WELL_FED);
        }

        if (random.nextDouble() > totalLevel * config.getWellFedRatePerLevel()) {
            return;
        }

        int result = player.getFoodLevel() + 1;
        if (result <= 0) {
            return;
        }

        FoodLevelChangeEvent foodLevelChangeEvent = new FoodLevelChangeEvent(player, result);
        plugin.getServer().getPluginManager().callEvent(foodLevelChangeEvent);
        if (!foodLevelChangeEvent.isCancelled()) {
            player.setFoodLevel(Math.max(0, Math.min(foodLevelChangeEvent.getFoodLevel(), 20)));
        }
    }
}
