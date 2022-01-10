package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.PoisonArrowConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonArrow extends ProjectileHitEventHandler<PoisonArrowConfig> {

    public PoisonArrow(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getPoisonArrowConfig());
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

        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, config.getEffectDurationTick(level),
                config.getEffectAmplifier(level)));

        if (shooter instanceof Player && plugin.getPlayerData().getNotifications((Player) shooter)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(shooter).enchant.poisoned.sendTo(shooter);
        }

        if (generalConfig.isParticlesEnabled()) {
            target.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, target.getEyeLocation(), 10, 1.0F, 1.0F, 1.0F,
                    0.1F);
            target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), 10, 1.0F, 1.0F, 1.0F, 0.1F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            target.getWorld().playSound(target.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, soundVolume, 0.8F);
        }
    }

    @Override
    public Particle getTrailingParticle() {
        return null;
    }
}
