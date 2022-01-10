package net.okocraft.enchantsplus.listener;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.handler.Agility;
import net.okocraft.enchantsplus.enchant.handler.AutoJump;
import net.okocraft.enchantsplus.enchant.handler.AutoSmelt;
import net.okocraft.enchantsplus.enchant.handler.AutoSpeed;
import net.okocraft.enchantsplus.enchant.handler.Beastmaster;
import net.okocraft.enchantsplus.enchant.handler.Beheading;
import net.okocraft.enchantsplus.enchant.handler.Blindness;
import net.okocraft.enchantsplus.enchant.handler.BlindnessArrow;
import net.okocraft.enchantsplus.enchant.handler.ElectrocuteAndInsulation;
import net.okocraft.enchantsplus.enchant.handler.Excavation;
import net.okocraft.enchantsplus.enchant.handler.ExplodingArrow;
import net.okocraft.enchantsplus.enchant.handler.Updraft;
import net.okocraft.enchantsplus.enchant.handler.Harvesting;
import net.okocraft.enchantsplus.enchant.handler.HeavyCurse;
import net.okocraft.enchantsplus.enchant.handler.HungerCurse;
import net.okocraft.enchantsplus.enchant.handler.IceAspect;
import net.okocraft.enchantsplus.enchant.handler.LifeLeech;
import net.okocraft.enchantsplus.enchant.handler.Life;
import net.okocraft.enchantsplus.enchant.handler.NightVision;
import net.okocraft.enchantsplus.enchant.handler.PlasmaAndInsulation;
import net.okocraft.enchantsplus.enchant.handler.PoisonArrow;
import net.okocraft.enchantsplus.enchant.handler.Quake;
import net.okocraft.enchantsplus.enchant.handler.RapidFire;
import net.okocraft.enchantsplus.enchant.handler.Regain;
import net.okocraft.enchantsplus.enchant.handler.Regeneration;
import net.okocraft.enchantsplus.enchant.handler.Reinforced;
import net.okocraft.enchantsplus.enchant.handler.Rejuvination;
import net.okocraft.enchantsplus.enchant.handler.Slowness;
import net.okocraft.enchantsplus.enchant.handler.Soulbound;
import net.okocraft.enchantsplus.enchant.handler.UnstableCurse;
import net.okocraft.enchantsplus.enchant.handler.Venom;
import net.okocraft.enchantsplus.enchant.handler.WaterBreathing;
import net.okocraft.enchantsplus.enchant.handler.WellFed;
import net.okocraft.enchantsplus.enchant.handler.WitheringArrow;
import net.okocraft.enchantsplus.enchant.handler.Withering;
import net.okocraft.enchantsplus.enchant.handler.XPBoost;
import net.okocraft.enchantsplus.event.PlayerTickEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MainListener implements Listener {

    public final EnchantsPlus plugin;

    public final Agility agilityHandler;
    public final AutoJump autoJumpHandler;
    public final AutoSmelt autoSmeltHandler;
    public final AutoSpeed autoSpeedHandler;
    public final Beastmaster beastmasterHandler;
    public final Beheading beheadingHandler;
    public final Blindness blindnessHandler;
    public final BlindnessArrow blindnessArrowHandler;
    public final ElectrocuteAndInsulation electrocuteHandler;
    public final Excavation excavationHandler;
    public final ExplodingArrow explodingArrowHandler;
    public final Harvesting harvestingHandler;
    public final HeavyCurse heavyCurseHandler;
    public final HungerCurse hungerCurseHandler;
    public final IceAspect iceAspectHandler;
    public final Life lifeHandler;
    public final LifeLeech lifeLeechHandler;
    public final NightVision nightVisionHandler;
    public final PlasmaAndInsulation plasmaHandler;
    public final PoisonArrow poisonArrowHandler;
    public final Quake quakeHandler;
    public final RapidFire rapidFireHandler;
    public final Regain regainHandler;
    public final Regeneration regenerationHandler;
    public final Reinforced reinforcedHandler;
    public final Rejuvination rejuvinationHandler;
    public final Slowness slownessHandler;
    public final Soulbound soulboundHandler;
    public final UnstableCurse unstableCurseHandler;
    public final Updraft updraftHandler;
    public final Venom venomHandler;
    public final WaterBreathing waterBreathingHandler;
    public final WellFed wellFedHandler;
    public final Withering witheringHandler;
    public final WitheringArrow witheringArrowHandler;
    public final XPBoost xpBoostHandler;

    public MainListener(EnchantsPlus plugin) {
        this.plugin = plugin;
        this.agilityHandler = new Agility(plugin);
        this.autoJumpHandler = new AutoJump(plugin);
        this.autoSmeltHandler = new AutoSmelt(plugin);
        this.autoSpeedHandler = new AutoSpeed(plugin);
        this.beastmasterHandler = new Beastmaster(plugin);
        this.beheadingHandler = new Beheading(plugin);
        this.blindnessArrowHandler = new BlindnessArrow(plugin);
        this.blindnessHandler = new Blindness(plugin);
        this.electrocuteHandler = new ElectrocuteAndInsulation(plugin);
        this.excavationHandler = new Excavation(plugin);
        this.explodingArrowHandler = new ExplodingArrow(plugin);
        this.harvestingHandler = new Harvesting(plugin);
        this.heavyCurseHandler = new HeavyCurse(plugin);
        this.hungerCurseHandler = new HungerCurse(plugin);
        this.iceAspectHandler = new IceAspect(plugin);
        this.lifeHandler = new Life(plugin);
        this.lifeLeechHandler = new LifeLeech(plugin);
        this.nightVisionHandler = new NightVision(plugin);
        this.plasmaHandler = new PlasmaAndInsulation(plugin);
        this.poisonArrowHandler = new PoisonArrow(plugin);
        this.quakeHandler = new Quake(plugin);
        this.rapidFireHandler = new RapidFire(plugin);
        this.regainHandler = new Regain(plugin);
        this.regenerationHandler = new Regeneration(plugin);
        this.reinforcedHandler = new Reinforced(plugin);
        this.rejuvinationHandler = new Rejuvination(plugin);
        this.slownessHandler = new Slowness(plugin);
        this.soulboundHandler = new Soulbound(plugin);
        this.unstableCurseHandler = new UnstableCurse(plugin);
        this.updraftHandler = new Updraft(plugin);
        this.venomHandler = new Venom(plugin);
        this.waterBreathingHandler = new WaterBreathing(plugin);
        this.wellFedHandler = new WellFed(plugin);
        this.witheringArrowHandler = new WitheringArrow(plugin);
        this.witheringHandler = new Withering(plugin);
        this.xpBoostHandler = new XPBoost(plugin);
    }

    @EventHandler
    public void onMainTimerTick(PlayerTickEvent event) {
        agilityHandler.handle(event);
        autoJumpHandler.handle(event);
        autoSpeedHandler.handle(event);
        harvestingHandler.handle(event);
        heavyCurseHandler.handle(event);
        hungerCurseHandler.handle(event);
        lifeHandler.handle(event);
        nightVisionHandler.handle(event);
        regenerationHandler.handle(event);
        rejuvinationHandler.handle(event);
        unstableCurseHandler.handle(event);
        updraftHandler.handle(event);
        waterBreathingHandler.handle(event);
        wellFedHandler.handle(event);
    }

    @EventHandler
    public void onBlockBreakAndDrop(BlockDropItemEvent event) {
        regainHandler.handle(event);
        autoSmeltHandler.handle(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        beastmasterHandler.handle(event);
        blindnessHandler.handle(event);
        electrocuteHandler.handle(event);
        iceAspectHandler.handle(event);
        lifeLeechHandler.handle(event);
        plasmaHandler.handle(event);
        slownessHandler.handle(event);
        venomHandler.handle(event);
        witheringHandler.handle(event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        beheadingHandler.handle(event);
        xpBoostHandler.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityShootBow(EntityShootBowEvent event) {
        blindnessArrowHandler.shoot(event);
        explodingArrowHandler.shoot(event);
        poisonArrowHandler.shoot(event);
        rapidFireHandler.handle(event);
        witheringArrowHandler.shoot(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        blindnessArrowHandler.handle(event);
        explodingArrowHandler.handle(event);
        poisonArrowHandler.handle(event);
        witheringArrowHandler.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        excavationHandler.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        quakeHandler.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDamage(BlockDamageEvent event) {
        reinforcedHandler.handle(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        soulboundHandler.handle(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        soulboundHandler.onRespawn(event);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        agilityHandler.removeTickCount(player);
        autoJumpHandler.removeTickCount(player);
        autoSpeedHandler.removeTickCount(player);
        heavyCurseHandler.removeTickCount(player);
        hungerCurseHandler.removeTickCount(player);
        lifeHandler.removeTickCount(player);
        nightVisionHandler.removeTickCount(player);
        regenerationHandler.removeTickCount(player);
        rejuvinationHandler.removeTickCount(player);
        unstableCurseHandler.removeTickCount(player);
        updraftHandler.removeTickCount(player);
        waterBreathingHandler.removeTickCount(player);
        wellFedHandler.removeTickCount(player);

        agilityHandler.removeAttributeModifier(player);
        autoSpeedHandler.removeAttributeModifier(player);
        lifeHandler.removeAttributeModifier(player);

        plugin.getPlayerData().clearCache();
    }
}
