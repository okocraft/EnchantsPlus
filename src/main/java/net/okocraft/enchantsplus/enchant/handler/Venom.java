package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.VenomConfig;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Venom extends EntityDamageByEntityEventHandler<VenomConfig> {

    public Venom(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getVenomConfig());
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

        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, config.getEffectDurationTick(level), 0));

        if (user instanceof Player && plugin.getPlayerData().getNotifications((Player) user)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(user).enchant.poisoned.sendTo(user);
        }

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, victim.getEyeLocation(), 50, 1.5, 1.5, 1.5, 0.1);
            victim.getWorld().spawnParticle(Particle.SPELL_WITCH, victim.getEyeLocation(), 20, 1.5, 1.5, 1.5, 0.1);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            victim.getWorld().playSound(victim.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, soundVolume, 0.8F);
        }
    }
}
