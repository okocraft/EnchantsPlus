package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import net.okocraft.enchantsplus.config.Config;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RapidFire extends EnchantPlusHandler<Config.RapidFireConfig, EntityShootBowEvent> {

    private static final NamespacedKey RAPID_FIRE_ARROW_KEY =
            Objects.requireNonNull(NamespacedKey.fromString("rapid_fire_arrow"), "Invalid key");

    public RapidFire(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getRapidFireConfig());
    }

    @Override
    public Class<EntityShootBowEvent> getEventClass() {
        return EntityShootBowEvent.class;
    }

    @Override
    public void handle(EntityShootBowEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return;
        }

        LocalItemStack bow = plugin.wrapItem(event.getBow());
        int arrows = bow.getCustomEnchantLevel(EnchantPlus.RAPID_FIRE);
        if (arrows == 0) {
            return;
        }

        Arrow arrow = (Arrow) event.getProjectile();
        PersistentDataContainer container = arrow.getPersistentDataContainer();

        if (container.getOrDefault(RAPID_FIRE_ARROW_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 1) {
            return;
        }

        PotionType potionType = arrow.getBasePotionType();
        final Color color;
        Color temp;
        try {
            temp = arrow.getColor();
        } catch (IllegalArgumentException e) {
            temp = null;
        }
        color = temp;
        List<PotionEffect> customEffects = new ArrayList<>(arrow.getCustomEffects());
        double damage = arrow.getDamage();
        int fireTicks = arrow.getFireTicks();
        int freezeTicks = arrow.getFreezeTicks();
        int knockbackStrength = arrow.getKnockbackStrength();
        int pierceLevel = arrow.getPierceLevel();
        Vector velocity = arrow.getVelocity();
        double velocityLength = velocity.length();
        boolean hasGravity = arrow.hasGravity();
        boolean isCritical = arrow.isCritical();
        boolean isGlowing = arrow.isGlowing();
        boolean isInvulnerable = arrow.isInvulnerable();
        boolean isSilent = arrow.isSilent();
        PickupStatus pickupStatus = arrow.getPickupStatus();
        boolean isPersistent = arrow.isPersistent();
        ProjectileSource shooter = arrow.getShooter();
        boolean isShotFromCrossbow = arrow.isShotFromCrossbow();
        boolean isVisualFire = arrow.isVisualFire();


        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {

                EntityEquipment equipment = event.getEntity().getEquipment();
                if (equipment == null) {
                    cancel();
                    return;
                }
                ItemStack currentHand = equipment.getItem(event.getHand());
                if (count >= arrows || currentHand == null || !currentHand.isSimilar(event.getBow())) {
                    cancel();
                    return;
                }

                Arrow another = event.getEntity().launchProjectile(arrow.getClass());
                another.getPersistentDataContainer().set(RAPID_FIRE_ARROW_KEY, PersistentDataType.BYTE, (byte) 1);
                if (potionType != PotionType.UNCRAFTABLE) {
                    another.setBasePotionType(potionType);
                }
                if (color != null) {
                    another.setColor(color);
                }
                customEffects.forEach(effect -> another.addCustomEffect(effect, true));
                another.setDamage(damage);
                another.setFireTicks(fireTicks);
                another.setFreezeTicks(freezeTicks);
                another.setKnockbackStrength(knockbackStrength);
                another.setPierceLevel(pierceLevel);
                another.setVelocity(event.getEntity().getEyeLocation().getDirection().multiply(velocityLength));
                another.setGravity(hasGravity);
                another.setCritical(isCritical);
                another.setGlowing(isGlowing);
                another.setInvulnerable(isInvulnerable);
                another.setSilent(isSilent);
                another.setPickupStatus(pickupStatus);
                another.setPersistent(isPersistent);
                another.setShooter(shooter);
                another.setShotFromCrossbow(isShotFromCrossbow);
                another.setVisualFire(isVisualFire);

                EntityShootBowEvent anotherEvent = new EntityShootBowEvent(
                        event.getEntity(),
                        event.getBow(),
                        event.getConsumable(),
                        another,
                        event.getHand(),
                        event.getForce(),
                        event.shouldConsumeItem()
                );
                plugin.getServer().getPluginManager().callEvent(anotherEvent);
                if (anotherEvent.isCancelled() || !anotherEvent.getProjectile().equals(another)) {
                    another.remove();
                    return;
                }
                if (anotherEvent.shouldConsumeItem()) {
                    LivingEntity anotherShooter = anotherEvent.getEntity();
                    if (anotherShooter instanceof HumanEntity human) {
                        var consumable = anotherEvent.getConsumable();

                        if (consumable == null || consumable.getType().isAir() || consumable.getAmount() == 0) {
                            another.remove();
                            cancel();
                            return;
                        }

                        var copied = consumable.clone();
                        copied.setAmount(1);

                        if (!human.getInventory().removeItem(copied).isEmpty()) {
                            another.remove();
                            cancel();
                            return;
                        }
                        callItemDamageEvent((Player) human, anotherEvent.getBow(), 1);
                    }
                }
                count++;
            }
        }.runTaskTimer(plugin, 8L, 4L);
    }
}
