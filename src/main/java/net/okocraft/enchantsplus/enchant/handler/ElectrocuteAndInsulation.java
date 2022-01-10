package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.ElectrocuteConfig;
import net.okocraft.enchantsplus.config.Config.InsulationConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ElectrocuteAndInsulation extends EntityDamageByEntityEventHandler<ElectrocuteConfig> {

    public ElectrocuteAndInsulation(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getElectrocuteConfig());
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

        victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, config.getEffectDurationTick(level), 0));

        if (user instanceof Player && plugin.getPlayerData().getNotifications((Player) user)
                && !generalConfig.disableNotificationsGlobally()) {
            plugin.getLanguagesConfig().getLanguage(user).enchant.electrocuteConcussion.sendTo(user);
        }

        InsulationConfig insulationConfig = plugin.getMainConfig().getInsulationConfig();

        var victimEquipment = victim.getEquipment();

        if (victimEquipment == null) {
            return;
        }

        double ironDamage = config.getIronDamage(level);
        double goldDamage = config.getGoldDamage(level);
        double chainDamage = config.getChainDamage(level);
        double damage = 0.0D;

        for (ItemStack equipment : victimEquipment.getArmorContents()) {
            if (equipment.getType().name().startsWith("IRON_")) {
                damage += ironDamage;
            } else if (equipment.getType().name().startsWith("GOLD_")) {
                damage += goldDamage;
            } else if (equipment.getType().name().startsWith("CHAIN_")) {
                damage += chainDamage;
            }

            LocalItemStack local = plugin.wrapItem(equipment);

            if (local != null) {
                damage = damage * insulationConfig.getElectrocuteMultiplier()
                        - insulationConfig.getExtraDecrement(local.getCustomEnchantLevel(EnchantPlus.INSULATION));
            }
        }

        if (damage <= 0D) {
            return;
        }

        event.setDamage(event.getDamage() + damage);

        if (generalConfig.isParticlesEnabled()) {
            victim.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, victim.getEyeLocation(), 25, 1.5F, 1.5F, 1.5F, 0.1F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            victim.getWorld().playSound(victim.getEyeLocation(), Sound.ENTITY_GUARDIAN_ATTACK, soundVolume, 1.75F);
        }
    }
}
