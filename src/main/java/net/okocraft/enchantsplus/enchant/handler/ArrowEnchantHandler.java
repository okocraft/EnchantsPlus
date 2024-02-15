package net.okocraft.enchantsplus.enchant.handler;

import io.papermc.paper.util.Tick;
import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.BowActivatableEnchantConfig;
import net.okocraft.enchantsplus.config.Config.BowEnchantConfig;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class ArrowEnchantHandler<C extends BowEnchantConfig>
        extends EnchantPlusHandler<C, ProjectileHitEvent> {

    private static final long TRAILING_PARTICLE_EXPIRES = Tick.of(48L).toNanos();

    public ArrowEnchantHandler(EnchantsPlus plugin, C config) {
        super(plugin, config);
    }

    @Override
    public Class<ProjectileHitEvent> getEventClass() {
        return ProjectileHitEvent.class;
    }

    public abstract Particle getTrailingParticle();

    public void shoot(EntityShootBowEvent event) {
        if (event.isCancelled() || !config.isEnabled() || isDisabledByWorldGuard(event.getEntity(), event.getEntity().getLocation())) {
            return;
        }

        LocalItemStack bow = plugin.wrapItem(event.getBow());
        if (bow == null || !bow.hasCustomEnchant(config.getType())) {
            return;
        }

        if (config instanceof BowActivatableEnchantConfig baeConfig) {
            if (random.nextDouble() >= baeConfig.getActivationRate(bow.getCustomEnchantLevel(config.getType()))) {
                return;
            }
        }

        Entity projectile = event.getProjectile();

        Particle trailingParticle = getTrailingParticle();
        if (trailingParticle != null && plugin.getMainConfig().getGeneralConfig().isParticlesEnabled()) {
            long expires = System.nanoTime() + TRAILING_PARTICLE_EXPIRES;
            projectile.getScheduler().runAtFixedRate(this.plugin, task -> {
                if (expires < System.nanoTime() || projectile.isDead() || projectile.getVelocity().lengthSquared() < 0.000025D) {
                    task.cancel();
                } else {
                    projectile.getWorld().spawnParticle(this.getTrailingParticle(), projectile.getLocation(), 5, 0.1F, 0.1F, 0.1F, 0.05F);
                }
            }, null, 1L, 1L);
        }

        projectile.setMetadata("enchantplus_" + config.getType().getId(),
                new FixedMetadataValue(plugin, bow.getCustomEnchantLevel(config.getType())));
        if (!projectile.hasMetadata("enchantplus_force")) {
            projectile.setMetadata("enchantplus_force",
                    new FixedMetadataValue(plugin, event.getForce()));
        }
    }

}
