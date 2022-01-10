package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.XPBoostConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public class XPBoost extends EnchantPlusHandler<XPBoostConfig, EntityDeathEvent> {
    
    public XPBoost(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getXpBoostConfig());
    }

    @Override
    public Class<EntityDeathEvent> getEventClass() {
        return EntityDeathEvent.class;
    }

    @Override
    public void handle(EntityDeathEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        Player user = event.getEntity().getKiller();
        if (user == null) {
            return;
        }

        LivingEntity victim = event.getEntity();
        LocalItemStack handItem = plugin.wrapItem(user.getEquipment().getItemInMainHand());
        if (!isValidTool(handItem) || isDisabledByWorldGuard(user, victim.getLocation()) || !config.canUseFor(victim)) {
            return;
        }

        int level = handItem.getCustomEnchantLevel(EnchantPlus.XP_BOOST);
        event.setDroppedExp((int) (event.getDroppedExp() * config.getBoostMultiplier(level)));

    }
}
