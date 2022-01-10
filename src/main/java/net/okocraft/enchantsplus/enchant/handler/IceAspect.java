package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.IceAspectConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceAspect extends EntityDamageByEntityEventHandler<IceAspectConfig> {

    public IceAspect(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getIceAspectConfig());
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

        var user = context.user();
        var victim = context.victim();
        int level = context.level();

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, config.getEffectDurationTick(level), 50));
        if (user instanceof Player && plugin.getPlayerData().getNotifications((Player) user)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(user).enchant.frozen.sendTo(user);
        }

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, victim.getEyeLocation(), 20, 1F, 1F, 1F, 0F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            victim.getWorld().playSound(victim.getEyeLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, soundVolume,
                    1.75F);
        }
    }
}
