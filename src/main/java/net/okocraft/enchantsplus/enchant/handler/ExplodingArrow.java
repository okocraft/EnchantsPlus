package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.ExplodingArrowConfig;
import org.bukkit.Particle;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;

public class ExplodingArrow extends ProjectileHitEventHandler<ExplodingArrowConfig> {

    public ExplodingArrow(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getExplodingArrowConfig());
    }

    @Override
    public void handle(ProjectileHitEvent event) {
        var context = checkEventAndCreateContext(event);

        if (context.shouldIgnore()) {
            return;
        }

        var projectile = context.projectile();
        int level = context.level();

        float force = 0;

        for (MetadataValue metadataValue : projectile.getMetadata("enchantplus_force")) {
            force = Math.max(force, metadataValue.asFloat());
        }

        if (force == 0) {
            return;
        }

        boolean explosionOccurred = projectile.getWorld().createExplosion(
                projectile.getLocation(),
                (float) config.getExplosionStrength(level) * force,
                false,
                config.explodesBlocks()
        );

        if (explosionOccurred) {
            projectile.remove();
        }
    }

    @Override
    public Particle getTrailingParticle() {
        return Particle.FIREWORKS_SPARK;
    }
}
