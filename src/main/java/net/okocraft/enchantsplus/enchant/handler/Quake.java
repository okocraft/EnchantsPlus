package net.okocraft.enchantsplus.enchant.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.QuakeConfig;
import net.okocraft.enchantsplus.config.Languages.Language;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Quake extends EnchantPlusHandler<QuakeConfig, PlayerInteractEvent> {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public Quake(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getQuakeConfig());
    }

    @Override
    public Class<PlayerInteractEvent> getEventClass() {
        return PlayerInteractEvent.class;
    }

    @Override
    public void handle(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY || !config.isEnabled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        LocalItemStack usedItem = plugin.wrapItem(event.getItem());
        if (!isValidTool(usedItem)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null
                && config.prioritizesWorldInteraction()
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && block.getType().isInteractable()) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Location location = clickedBlock.getLocation();

        Player user = event.getPlayer();
        Language language = plugin.getLanguagesConfig().getLanguage(user);

        if (isDisabledByWorldGuard(user, location)) {
            language.enchant.disabled.sendTo(user, config.getDisplayName());
            return;
        }

        if (config.hasCooldown()) {
            long previousActivation = cooldowns.getOrDefault(user.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();
            int cooldown = config.getCooldown() * 1000;
            if (previousActivation + cooldown > currentTime) {
                language.enchant.quakeOnCooldown.sendTo(user, (int) (previousActivation + cooldown - currentTime) / 1000);
                return;
            }
            cooldowns.put(user.getUniqueId(), currentTime);
        }

        LocalItemStack handItem = plugin.wrapItem(event.getItem());
        if (!isValidTool(handItem)) {
            return;
        }

        event.setCancelled(true);

        callItemDamageEvent(user, handItem.getItem(), config.getDurabilityCost());

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, soundVolume, 0.8F);
        }

        int level = handItem.getCustomEnchantLevel(config.getType());
        int explosionRadius = config.getExplosionSize(level);

        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 3, 2, 2, 2, 0);

        Location loc = location.clone().add(-explosionRadius, -3, -explosionRadius);
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (int dx = 0; dx < 2 * explosionRadius; dx++) {
            loc.setX(x + dx);
            
            for (int dy = 0; dy < 6; dy++) {
                loc.setY(y + dy);
                
                for (int dz = 0; dz < 2 * explosionRadius; dz++) {
                    loc.setZ(z + dz);

                    if (!loc.getWorld().getBlockAt(loc).getType().isAir()) {
                        if (random.nextDouble() <= 0.3) {
                            loc.getWorld().spawnParticle(Particle.CLOUD, loc, 10, 1, 1, 1, 0);
                        }
                    }

                }
            }
        }

        for (Entity nerbyEntity : location.getWorld().getNearbyEntities(location, explosionRadius, 3.0D, explosionRadius)) {
            if (!(nerbyEntity instanceof LivingEntity) || nerbyEntity.equals(user)) {
                continue;
            }
            Location entityLocation = nerbyEntity.getLocation();
            if (isDisabledByWorldGuard(user, entityLocation)) {
                continue;
            }
            Vector vector = new Vector(0, 1, 0);
            int nerbyLocationX = entityLocation.getBlockX();
            int nerbyLocationZ = entityLocation.getBlockZ();
            int locationX = location.getBlockX();
            int locationZ = location.getBlockZ();
            double velocity = config.getVelocity(level);

            if (locationZ > nerbyLocationZ) {
                vector.setZ(-velocity);
            } else if (locationZ < nerbyLocationZ) {
                vector.setZ(velocity);
            }
            if (locationX > nerbyLocationX) {
                vector.setX(-velocity);
            } else if (locationX < nerbyLocationX) {
                vector.setX(velocity);
            }

            if (nerbyEntity instanceof Player) {
                if (config.launchesPlayers() && plugin.getBridgeManager().getWorldguardBridge().canAttack((Player) nerbyEntity)) {
                    nerbyEntity.setVelocity(vector);
                    if (config.doesFallDamageAffectPlayers()) {
                        nerbyEntity.setFallDistance((float) config.getFallDamage());
                    } else {
                        nerbyEntity.setFallDistance(-4096.0F);
                    }
                }
            } else if (config.launchesOtherEntities()) {
                nerbyEntity.setVelocity(vector);
            }
        }
    }
}
