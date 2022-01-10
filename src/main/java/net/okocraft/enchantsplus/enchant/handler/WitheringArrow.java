package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.WitheringArrowConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitheringArrow extends ProjectileHitEventHandler<WitheringArrowConfig> {

    public WitheringArrow(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getWitheringArrowConfig());
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

        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, config.getEffectDurationTick(level), config.getEffectAmplifier(level)));

        if (shooter instanceof Player && plugin.getPlayerData().getNotifications((Player) shooter)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(shooter).enchant.withered.sendTo(shooter);
        }

        if (generalConfig.isParticlesEnabled()) {
            target.getWorld().spawnParticle(Particle.SUSPENDED_DEPTH, target.getEyeLocation(), 50, 1.0F, 1.0F, 1.0F, 0.1F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            target.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_BLAZE_HURT, soundVolume, 0.5F);
        }
    }

    @Override
    public Particle getTrailingParticle() {
        return null;
    }
}
