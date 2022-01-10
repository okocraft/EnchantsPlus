package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EntityDamageByEntityEventHandler<C extends Config.WeaponActivatableEnchantConfig> extends EnchantPlusHandler<C, EntityDamageByEntityEvent> {

    private static final EventContext IGNORING = new EventContext();

    public EntityDamageByEntityEventHandler(@NotNull EnchantsPlus plugin, @NotNull C config) {
        super(plugin, config);
    }

    protected @NotNull EventContext checkEventAndCreateContext(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return IGNORING;
        }

        if (!(event.getDamager() instanceof LivingEntity user) || !(event.getEntity() instanceof LivingEntity victim)) {
            return IGNORING;
        }

        var equipment = user.getEquipment();

        if (equipment == null) {
            return IGNORING;
        }

        LocalItemStack handItem = plugin.wrapItem(equipment.getItemInMainHand());

        if (!isValidTool(handItem) ||
                isDisabledByWorldGuard(user, victim.getLocation()) ||
                !config.canUseFor(victim)) {
            return IGNORING;
        }

        if (victim instanceof Player && !generalConfig.affectsPlayerToPlayer()) {
            return IGNORING;
        }

        if (event.getFinalDamage() == 0.0D) {
            return IGNORING;
        }

        int level = handItem.getCustomEnchantLevel(config.getType());

        if (random.nextDouble() > config.getActivationRate(level)) {
            return IGNORING;
        } else {
            return new EventContext(false, user, victim, handItem, level);
        }
    }

    protected record EventContext(boolean shouldIgnore, LivingEntity user, LivingEntity victim,
                                LocalItemStack tool, int level) {

        private EventContext() {
            this(true, null, null, null, 0);
        }
    }
}
