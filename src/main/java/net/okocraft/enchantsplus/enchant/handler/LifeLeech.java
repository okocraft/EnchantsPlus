package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.LifeLeechConfig;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LifeLeech extends EntityDamageByEntityEventHandler<LifeLeechConfig> {

    public LifeLeech(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getLifeLeechConfig());
    }

    @Override
    public Class<EntityDamageByEntityEvent> getEventClass() {
        return EntityDamageByEntityEvent.class;
    }

    @Override
    public void handle(EntityDamageByEntityEvent event) {
        var context = checkEventAndCreateContext(event);

        if (context.shouldIgnore()) {
            return;
        }

        var handItem = context.tool();
        var user = context.user();
        var victim = context.victim();

        int level = handItem.getCustomEnchantLevel(config.getType());
        if (random.nextDouble() > config.getActivationRate(level)) {
            return;
        }

        var attribute = user.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute == null) {
            return;
        }

        double amount = config.getLifeLeechHealAmount();
        double health = user.getHealth();
        double maxHealth = attribute.getValue();

        user.setHealth(Math.min(maxHealth, health + amount));

        amount = user.getHealth() - health;

        if (user instanceof Player && plugin.getPlayerData().getNotifications((Player) user)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(user).enchant.leeched.sendTo(user, amount);
        }

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, victim.getEyeLocation(), 30, 1.5F, 1.5F, 1.5F, 0.1F);
        }
    }
}
