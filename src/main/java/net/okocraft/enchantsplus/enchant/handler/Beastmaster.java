package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.BeastmasterConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class Beastmaster extends EnchantPlusHandler<BeastmasterConfig, EntityDamageByEntityEvent> {

    public Beastmaster(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getBeastmasterConfig());
    }

    @Override
    public Class<EntityDamageByEntityEvent> getEventClass() {
        return EntityDamageByEntityEvent.class;
    }

    @Override
    public void handle(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return;
        }

        double damageIncrease = calculateEnchantEffect(event.getDamager(), true);
        double damageDecrease = calculateEnchantEffect(event.getEntity(), false);

        if (damageIncrease == damageDecrease) {
            return;
        }

        double damage = event.getDamage() + damageIncrease - damageDecrease;

        if (damage < 0.0D) {
            damage = 0.0D;
        }

        event.setDamage(damage);
    }

    private double calculateEnchantEffect(@NotNull Entity entity, boolean increase) {
        if (!(entity instanceof Tameable tameable) ||
                !tameable.isTamed() ||
                !(tameable.getOwner() instanceof HumanEntity owner)
        ) {
            return 0D;
        }

        LocalItemStack head = plugin.wrapItem(owner.getInventory().getHelmet());

        if (!isValidTool(head)) {
            return 0D;
        }

        int level = head.getCustomEnchantLevel(EnchantPlus.BEASTMASTER);
        return increase ? config.getDamageIncrease(level) : config.getDamageDecrease(level);
    }
}
