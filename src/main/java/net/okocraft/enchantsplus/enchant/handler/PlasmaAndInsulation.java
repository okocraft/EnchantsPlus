package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.InsulationConfig;
import net.okocraft.enchantsplus.config.Config.PlasmaConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlasmaAndInsulation extends EntityDamageByEntityEventHandler<PlasmaConfig> {

    public PlasmaAndInsulation(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getPlasmaConfig());
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

        var victim = context.victim();
        int level = context.level();

        InsulationConfig insulationConfig = plugin.getMainConfig().getInsulationConfig();

        var victimEquipment = victim.getEquipment();

        if (victimEquipment == null) {
            return;
        }

        double leatherDamage = config.getLeatherDamage(level);
        double diamondDamage = config.getDiamondDamage(level);
        double damage = 0.0D;

        for (ItemStack equipment : victim.getEquipment().getArmorContents()) {
            if (equipment.getType().name().startsWith("LEATHER_")) {
                damage += leatherDamage;
            } else if (equipment.getType().name().startsWith("DIAMOND_")) {
                damage += diamondDamage;
            }
            LocalItemStack local = plugin.wrapItem(equipment);
            if (local != null) {
                damage = damage * insulationConfig.getPlasmaMultiplier()
                        - insulationConfig.getExtraDecrement(local.getCustomEnchantLevel(EnchantPlus.INSULATION));
            }
        }

        if (damage <= 0D) {
            return;
        }

        event.setDamage(event.getDamage() + damage);

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, victim.getEyeLocation(), 25, 0.8F, 0.4F, 0.8F,
                    0.0F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            victim.getWorld().playSound(victim.getEyeLocation(), Sound.ENTITY_GUARDIAN_ATTACK, soundVolume, 1.3F);
        }
    }
}
