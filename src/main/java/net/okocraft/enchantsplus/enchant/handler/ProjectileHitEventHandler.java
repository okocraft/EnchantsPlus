package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

public abstract class ProjectileHitEventHandler<C extends Config.BowActivatableEnchantConfig> extends ArrowEnchantHandler<C> {

    private static final EventContext IGNORING = new EventContext();

    private final String metaDataKey;

    public ProjectileHitEventHandler(@NotNull EnchantsPlus plugin, @NotNull C config) {
        super(plugin, config);
        metaDataKey = "enchantplus_" + config.getType().getId();
    }

    protected @NotNull EventContext checkEventAndCreateContext(@NotNull ProjectileHitEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return IGNORING;
        }

        Projectile projectile = event.getEntity();

        if (!(projectile.getShooter() instanceof LivingEntity shooter) ||
                isDisabledByWorldGuard(shooter, projectile.getLocation())) {
            return IGNORING;
        }

        int level = 0;
        for (MetadataValue metadataValue : projectile.getMetadata(metaDataKey)) {
            level = Math.max(level, metadataValue.asInt());
        }

        if (level == 0) {
            return IGNORING;
        }

        if (!(event.getHitEntity() instanceof LivingEntity target)) {
            return IGNORING;
        }

        return new EventContext(false, projectile, shooter, target, level);
    }

    protected record EventContext(boolean shouldIgnore, Projectile projectile, LivingEntity shooter,
                                  LivingEntity target, int level) {

        private EventContext() {
            this(true, null, null, null, 0);
        }
    }
}
