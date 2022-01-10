package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.HeavyCurseConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HeavyCurse extends TickDependentEnchantHandler<HeavyCurseConfig> {

    public HeavyCurse(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getHeavyCurseConfig());
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!config.isEnabled() || !checkTickInterval(player, 200)
                || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        int totalLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            LocalItemStack item = plugin.wrapItem(player.getEquipment().getItem(slot));
            if (!isValidTool(item)) {
                continue;
            }

            totalLevel += item.getCustomEnchantLevel(EnchantPlus.HEAVY_CURSE);
        }

        if (totalLevel == 0) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 260, totalLevel - 1));

        if (config.causesMiningFatigue()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 260, totalLevel - 1));
        }
    }
}
