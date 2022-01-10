package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.BeheadingConfig;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Beheading extends EnchantPlusHandler<BeheadingConfig, EntityDeathEvent> {

    public Beheading(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getBeheadingConfig());
    }

    @Override
    public Class<EntityDeathEvent> getEventClass() {
        return EntityDeathEvent.class;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void handle(EntityDeathEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        Player user = event.getEntity().getKiller();
        if (user == null) {
            return;
        }

        LivingEntity victim = event.getEntity();
        LocalItemStack handItem = plugin.wrapItem(user.getInventory().getItemInMainHand());
        if (!isValidTool(handItem) || isDisabledByWorldGuard(user, victim.getLocation()) || !config.canUseFor(victim)) {
            return;
        }

        if (random.nextDouble() >= config.getActivationRate(handItem.getCustomEnchantLevel(EnchantPlus.BEHEADING))) {
            return;
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (victim instanceof Monster) {
            if (victim instanceof Creeper) {
                head.setType(Material.CREEPER_HEAD);
                victim.getLocation().getWorld().dropItem(victim.getLocation(), head);
                return;
            }

            if (victim instanceof Zombie && !(victim instanceof PigZombie)) {
                head.setType(Material.ZOMBIE_HEAD);
                victim.getLocation().getWorld().dropItem(victim.getLocation(), head);
                return;
            }

            if (victim instanceof Skeleton && !(victim instanceof WitherSkeleton)) {
                head.setType(Material.SKELETON_SKULL);
                victim.getLocation().getWorld().dropItem(victim.getLocation(), head);
                return;
            }

            if (victim instanceof WitherSkeleton) {
                head.setType(Material.WITHER_SKELETON_SKULL);
                victim.getLocation().getWorld().dropItem(victim.getLocation(), head);
                return;
            }
        }

        if (!(head.getItemMeta() instanceof SkullMeta skullMeta)) {
            return;
        }

        if (victim instanceof Player) {
            skullMeta.setOwner(victim.getName());
        } else if (config.isMHFHeadEnabled()) {
            skullMeta = createMHFHead(victim, skullMeta);
        } else {
            skullMeta = null;
        }

        if (skullMeta != null) {
            head.setItemMeta(skullMeta);
            victim.getLocation().getWorld().dropItem(victim.getLocation(), head);
        }
    }

    @SuppressWarnings("deprecation")
    private @Nullable SkullMeta createMHFHead(@NotNull Entity victim, @NotNull SkullMeta skullMeta) {
        if (victim instanceof Ageable ageable && !ageable.isAdult()) {
            return null;
        }

        if (victim instanceof Squid) {
            skullMeta.setOwner("MHF_Squid");
            skullMeta.setDisplayName("§fSquid Head");
            return skullMeta;
        }

        if (victim instanceof Pig && !(victim instanceof PigZombie)) {
            skullMeta.setOwner("MHF_Pig");
            skullMeta.setDisplayName("§fPig Head");
            return skullMeta;
        }

        if (victim instanceof Chicken) {
            skullMeta.setOwner("MHF_Chicken");
            skullMeta.setDisplayName("§fChicken Head");
            return skullMeta;
        }

        if (victim instanceof Sheep) {
            skullMeta.setOwner("MHF_Sheep");
            skullMeta.setDisplayName("§fSheep Head");
            return skullMeta;
        }

        if (victim instanceof MushroomCow) {
            skullMeta.setOwner("MHF_MushroomCow");
            skullMeta.setDisplayName("§fMushroom Cow Head");
            return skullMeta;
        } else if (victim instanceof Cow) {
            skullMeta.setOwner("MHF_Cow");
            skullMeta.setDisplayName("§fCow Head");
            return skullMeta;
        }

        if (victim instanceof Villager) {
            skullMeta.setOwner("MHF_Villager");
            skullMeta.setDisplayName("§fVillager Head");
            return skullMeta;
        }

        if (victim instanceof CaveSpider) {
            skullMeta.setOwner("MHF_CaveSpider");
            skullMeta.setDisplayName("§fCave Spider Head");
            return skullMeta;
        } else if (victim instanceof Spider) {
            skullMeta.setOwner("MHF_Spider");
            skullMeta.setDisplayName("§fSpider Head");
            return skullMeta;
        }

        if (victim instanceof Blaze) {
            skullMeta.setOwner("MHF_Blaze");
            skullMeta.setDisplayName("§fBlaze Head");
            return skullMeta;
        }

        if (victim instanceof Enderman) {
            skullMeta.setOwner("MHF_Enderman");
            skullMeta.setDisplayName("§fEnderman Head");
            return skullMeta;
        }

        if (victim instanceof PigZombie) {
            skullMeta.setOwner("MHF_PigZombie");
            skullMeta.setDisplayName("§fPig Zombie Head");
            return skullMeta;
        }

        return null;
    }
}
