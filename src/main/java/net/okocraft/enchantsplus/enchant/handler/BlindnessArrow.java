package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.BlindnessArrowConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindnessArrow extends ProjectileHitEventHandler<BlindnessArrowConfig> {

    public BlindnessArrow(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getBlindnessArrowConfig());
    }

    @Override
    public Class<ProjectileHitEvent> getEventClass() {
        return ProjectileHitEvent.class;
    }

    @Override
    public void handle(ProjectileHitEvent event) {
        var context = checkEventAndCreateContext(event);

        if (context.shouldIgnore()) {
            return;
        }

        int level = context.level();
        var shooter = context.shooter();
        var target = context.target();

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, config.getEffectDurationTick(level), 0));

        if (shooter instanceof Player && plugin.getPlayerData().getNotifications((Player) shooter)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(shooter).enchant.blinded.sendTo(shooter);
        }

        if (generalConfig.isParticlesEnabled()) {
            target.getWorld().spawnParticle(Particle.CLOUD, target.getEyeLocation(), 10, 1.0F, 1.0F, 1.0F, 0.1F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            target.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_GUARDIAN_HURT, soundVolume, 0.8F);
        }
    }

    @Override
    public Particle getTrailingParticle() {
        return null;
    }
}
