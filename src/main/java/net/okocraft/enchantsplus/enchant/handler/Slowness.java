package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.SlownessConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Slowness extends EntityDamageByEntityEventHandler<SlownessConfig> {

    public Slowness(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getSlownessConfig());
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

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, config.getEffectDurationTick(level), config.getEffectAmplifier(level)));
        if (user instanceof Player && plugin.getPlayerData().getNotifications((Player) user)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(user).enchant.slowed.sendTo(user);
        }

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, victim.getEyeLocation(), 50, 1.5F, 1.5F, 1.5F, 0.1F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            victim.getWorld().playSound(victim.getEyeLocation(), Sound.ENTITY_ZOMBIE_INFECT, soundVolume, 0.5F);
        }
    }
}
