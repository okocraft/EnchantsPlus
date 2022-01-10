package net.okocraft.enchantsplus.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.enchant.EnchantPlus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import lombok.Getter;

public class Config extends CustomConfig {

    private final EnumMap<EnchantPlus, EnchantConfig> enchantConfigs = new EnumMap<>(EnchantPlus.class);

    @Getter
    private final GeneralConfig generalConfig = new GeneralConfig();
    @Getter
    private final AgilityConfig agilityConfig = new AgilityConfig();
    @Getter
    private final AutoJumpConfig autoJumpConfig = new AutoJumpConfig();
    @Getter
    private final AutoSmeltConfig autoSmeltConfig = new AutoSmeltConfig();
    @Getter
    private final AutoSpeedConfig autoSpeedConfig = new AutoSpeedConfig();
    @Getter
    private final BeastmasterConfig beastmasterConfig = new BeastmasterConfig();
    @Getter
    private final BeheadingConfig beheadingConfig = new BeheadingConfig();
    @Getter
    private final BlindnessConfig blindnessConfig = new BlindnessConfig();
    @Getter
    private final BlindnessArrowConfig blindnessArrowConfig = new BlindnessArrowConfig();
    @Getter
    private final ElectrocuteConfig electrocuteConfig = new ElectrocuteConfig();
    @Getter
    private final ExcavationConfig excavationConfig = new ExcavationConfig();
    @Getter
    private final ExplodingArrowConfig explodingArrowConfig = new ExplodingArrowConfig();
    @Getter
    private final UpdraftConfig updraftConfig = new UpdraftConfig();
    @Getter
    private final HarvestingConfig harvestingConfig = new HarvestingConfig();
    @Getter
    private final HeavyCurseConfig heavyCurseConfig = new HeavyCurseConfig();
    @Getter
    private final HungerCurseConfig hungerCurseConfig = new HungerCurseConfig();
    @Getter
    private final IceAspectConfig iceAspectConfig = new IceAspectConfig();
    @Getter
    private final InsulationConfig insulationConfig = new InsulationConfig();
    @Getter
    private final LifeLeechConfig lifeLeechConfig = new LifeLeechConfig();
    @Getter
    private final LifeConfig lifeConfig = new LifeConfig();
    @Getter
    private final RapidFireConfig rapidFireConfig = new RapidFireConfig();
    @Getter
    private final NightVisionConfig nightVisionConfig = new NightVisionConfig();
    @Getter
    private final PlasmaConfig plasmaConfig = new PlasmaConfig();
    @Getter
    private final PoisonArrowConfig poisonArrowConfig = new PoisonArrowConfig();
    @Getter
    private final QuakeConfig quakeConfig = new QuakeConfig();
    @Getter
    private final RegainConfig regainConfig = new RegainConfig();
    @Getter
    private final RegenerationConfig regenerationConfig = new RegenerationConfig();
    @Getter
    private final ReinforcedConfig reinforcedConfig = new ReinforcedConfig();
    @Getter
    private final RejuvinationConfig rejuvinationConfig = new RejuvinationConfig();
    @Getter
    private final SlownessConfig slownessConfig = new SlownessConfig();
    @Getter
    private final SoulboundConfig soulboundConfig = new SoulboundConfig();
    @Getter
    private final UnstableCurseConfig unstableCurseConfig = new UnstableCurseConfig();
    @Getter
    private final VenomConfig venomConfig = new VenomConfig();
    @Getter
    private final WaterBreathingConfig waterBreathingConfig = new WaterBreathingConfig();
    @Getter
    private final WellFedConfig wellFedConfig = new WellFedConfig();
    @Getter
    private final WitheringArrowConfig witheringArrowConfig = new WitheringArrowConfig();
    @Getter
    private final WitheringConfig witheringConfig = new WitheringConfig();
    @Getter
    private final XPBoostConfig xpBoostConfig = new XPBoostConfig();

    public Config(EnchantsPlus plugin) {
        super(plugin, "config.yml");
    }

    public EnchantConfig getBy(EnchantPlus enchant) {
        EnchantConfig config = enchantConfigs.get(enchant);
        if (config != null) {
            return config;
        }
        throw new IllegalStateException("There is no config class for the enchant " + enchant.getId());
    }

    @Override
    public void reload() {
        super.reload();
        autoSmeltConfig.reloadDisabledOre();
    }

    public class GeneralConfig {
        private GeneralConfig() {}

        public String getDefaultLanguage() {
            return get().getString("default-language");
        }

        public String getMessagePrefix() {
            return get().getString("messages-prefix");
        }
    
        public boolean isParticlesEnabled() {
            return get().getBoolean("particles-enabled");
        }
    
        public boolean isSoundsEnabled() {
            return get().getBoolean("sounds-enabled");
        }
        
        public int getSoundVolume() {
            return get().getInt("sounds-volume");
        }
        
        public boolean disableNotificationsGlobally() {
            return get().getBoolean("disable-notifications-globally");
        }
        
        public int getMiscTimerInterval() {
            return get().getInt("misc-timer-interval");
        }
        
        public int getRequiredLevel() {
            return get().getInt("required-level");
        }
        
        public boolean affectsPlayerToPlayer() {
            return get().getBoolean("affects-player-to-player");
        }
        
        public int getAbsoluteMaxEnchants() {
            return get().getInt("absolute-max-enchants");
        }

        public boolean getAbsoluteMaxEnchantsIgnoresCommand() {
            return get().getBoolean("ignores-enchant-command");
        }

        public boolean shouldMaxEnchantsIncludeVanillaEnchants() {
            return get().getBoolean("vanilla-enchants-included");
        }

        public double getEnchantingRate() {
            return get().getDouble("enchanting-rate");
        }

        public int getMaxCustomEnchants() {
            return get().getInt("max-custom-enchants");
        }

        public int getMaxCustomEnchantsOnBook() {
            return get().getInt("max-custom-enchants-on-book");
        }

        public boolean isCursedEnchantsEnabled() {
            return get().getBoolean("enabled-curse");
        }

        public boolean isHelpfulTooltipsEnabled() {
            return get().getBoolean("helpful-tooltips-enabled");
        }
    }

    public interface Activatable {
        double getBaseActivationRate();
        double getExtraActivationRatePerLevel();

        default double getActivationRate(int level) {
            return getBaseActivationRate() + getExtraActivationRatePerLevel() * (level - 1);
        }
    }

    public interface Effecter extends Activatable {
        double getBaseEffectDuration();
        double getExtraDurationPerLevel();
        default int getEffectDurationTick(int level) {
            return (int) (getBaseEffectDuration() + getExtraActivationRatePerLevel() * (level - 1)) * 20;
        }
    }

    public interface AmplifiedEffecter extends Effecter {
        int getBaseEffectAmplifier();
        int getExtraEffectAmplifierPerLevel();
        default int getEffectAmplifier(int level) {
            return getBaseEffectAmplifier() + getExtraEffectAmplifierPerLevel() * (level - 1);
        }
    }

    public abstract class EnchantConfig {
        @Getter
        final EnchantPlus type;

        private EnchantConfig(EnchantPlus type) {
            this.type = type;
            enchantConfigs.put(type, this);
        }

        protected String path(String key) {
            return "enchants." + type.getId() + "." + key;
        }

        public boolean isEnabled() {
            return get().getBoolean(path("enabled"), true);
        }

        public String getDisplayName() {
            return ChatColor.stripColor(get().getString(path("display-name"), type.getId()));
        }

        public int getEnchantChance() {
            return get().getInt(path("enchant-chance"), 0);
        }

        public int getMaxLevel() {
            return get().getInt(path("max-level"), 3);
        }

        public boolean isAnvilDisabled() {
            return get().getBoolean(path("disable-anvils"), false);
        }

        public boolean isLevelConbiningDisabled() {
            return get().getBoolean(path("disable-level-conbining"), false);
        }

        public boolean isGrindstoneDisabled() {
            return get().getBoolean(path("disable-grindstone"), false);
        }

    }

    public abstract class ArmorEnchantConfig extends EnchantConfig {
        private ArmorEnchantConfig(EnchantPlus type) {
            super(type);
        }

        public boolean worksInHand() {
            return get().getBoolean(path("work-in-hand"), false);
        }
    }

    public abstract class FightEnchantConfig extends EnchantConfig {
        private FightEnchantConfig(EnchantPlus type) {
            super(type);
        }

        public boolean canUseForPvP() {
            return get().getBoolean(path("use-pvp"), true);
        }

        public boolean canUseForPvE() {
            return get().getBoolean(path("use-pve"), true);
        }

        public boolean canUseFor(LivingEntity target) {
            return canUseForPvP() && target instanceof Player || canUseForPvE() && !(target instanceof Player);
        }
    }

    public abstract class WeaponEnchantConfig extends FightEnchantConfig {
        private WeaponEnchantConfig(EnchantPlus type) {
            super(type);
        }

        public boolean canEnchantOnAxe() {
            return get().getBoolean(path("on-axe"), true);
        }
    }

    public abstract class WeaponActivatableEnchantConfig extends WeaponEnchantConfig implements Activatable {
        private WeaponActivatableEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public double getBaseActivationRate() {
            return get().getDouble(path("base-activation-rate"));
        }
        
        @Override
        public double getExtraActivationRatePerLevel() {
            return get().getDouble(path("extra-activation-rate-per-level"));
        }
    }

    public abstract class WeaponEffecterEnchantConfig extends WeaponActivatableEnchantConfig implements Effecter {
        private WeaponEffecterEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public double getBaseEffectDuration() {
            return get().getDouble(path("base-effect-duration")) * 20;
        }
        
        @Override
        public double getExtraDurationPerLevel() {
            return get().getDouble(path("extra-duration-per-level")) * 20;
        }
    }

    public abstract class WeaponAmplifiedEffecterEnchantConfig extends WeaponEffecterEnchantConfig implements AmplifiedEffecter {
        private WeaponAmplifiedEffecterEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public int getBaseEffectAmplifier() {
            return get().getInt(path("base-effect-amplifier"));
        }
        
        @Override
        public int getExtraEffectAmplifierPerLevel() {
            return get().getInt(path("extra-effect-amplifier-per-level"));
        }
    }

    public abstract class BowEnchantConfig extends FightEnchantConfig {
        private BowEnchantConfig(EnchantPlus type) {
            super(type);
        }
    }

    public abstract class BowActivatableEnchantConfig extends BowEnchantConfig implements Activatable {
        private BowActivatableEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public double getBaseActivationRate() {
            return get().getDouble(path("base-activation-rate"));
        }
        
        @Override
        public double getExtraActivationRatePerLevel() {
            return get().getDouble(path("extra-activation-rate-per-level"));
        }
    }

    public abstract class BowEffecterEnchantConfig extends BowActivatableEnchantConfig implements Effecter {
        private BowEffecterEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public double getBaseEffectDuration() {
            return get().getDouble(path("base-effect-duration")) * 20;
        }
        
        @Override
        public double getExtraDurationPerLevel() {
            return get().getDouble(path("extra-duration-per-level")) * 20;
        }
    }

    public abstract class BowAmplifiedEffecterEnchantConfig extends BowEffecterEnchantConfig implements AmplifiedEffecter {
        private BowAmplifiedEffecterEnchantConfig(EnchantPlus type) {
            super(type);
        }

        @Override
        public int getBaseEffectAmplifier() {
            return get().getInt(path("base-effect-amplifier"));
        }
        
        @Override
        public int getExtraEffectAmplifierPerLevel() {
            return get().getInt(path("extra-effect-amplifier-per-level"));
        }
    }

    public class AgilityConfig extends EnchantConfig {
        private AgilityConfig() {
            super(EnchantPlus.AGILITY);
        }
    }

    public class AutoJumpConfig extends ArmorEnchantConfig {
        private AutoJumpConfig() {
            super(EnchantPlus.AUTO_JUMP);
        }
    }

    public class AutoSmeltConfig extends EnchantConfig {

        private List<Material> disabledOres = null;

        private AutoSmeltConfig() {
            super(EnchantPlus.AUTO_SMELT);
        }

        private void reloadDisabledOre() {
            List<Material> disabledOres = new ArrayList<>();
            for (String materialName : get().getStringList(path("disabled-ores"))) {
                Material material = Material.matchMaterial(materialName);
                if (material != null) {
                    disabledOres.add(Material.valueOf(materialName.toUpperCase()));
                }
            }
            this.disabledOres = disabledOres;
        }

        public List<Material> getDisabledOres() {
            if (this.disabledOres == null) {
                reloadDisabledOre();
            }

            return new ArrayList<>(this.disabledOres);
        }

        public boolean hasFortureSynergy() {
            return get().getBoolean(path("fortune-synergy"), true);
        }
    }

    public class AutoSpeedConfig extends ArmorEnchantConfig {
        private AutoSpeedConfig() {
            super(EnchantPlus.AUTO_SPEED);
        }

        @Override
        public boolean worksInHand() {
            return false;
        }
    }

    public class BeastmasterConfig extends ArmorEnchantConfig {
        private BeastmasterConfig() {
            super(EnchantPlus.BEASTMASTER);
        }

        public double getBaseDamageIncrease() {
            return get().getDouble(path("base-damage-increase"));
        }

        public double getDamageIncreaseMultiplier() {
            return get().getDouble(path("damage-increase-multiplier"));
        }

        public double getDamageIncrease(int level) {
            if (level == 1) {
                return getBaseDamageIncrease();
            } else {
                return getBaseDamageIncrease() * getDamageIncreaseMultiplier() * (level - 1);
            }
        }

        public double getBaseDamageDecrease() {
            return get().getDouble(path("base-damage-decrease"));
        }

        public double getDamageDecreaseMultiplier() {
            return get().getDouble(path("damage-decrease-multiplier"));
        }

        public double getDamageDecrease(int level) {
            if (level == 1) {
                return getBaseDamageDecrease();
            } else {
                return getBaseDamageDecrease() * getDamageDecreaseMultiplier() * (level - 1);
            }
        }
    }

    public class BeheadingConfig extends WeaponActivatableEnchantConfig {
        private BeheadingConfig() {
            super(EnchantPlus.BEHEADING);
        }

        public boolean isMHFHeadEnabled() {
            return get().getBoolean(path("enable-MHF-heads"));
        }
    }

    public class BlindnessConfig extends WeaponEffecterEnchantConfig {
        private BlindnessConfig() {
            super(EnchantPlus.BLINDNESS);
        }
    }

    public class BlindnessArrowConfig extends BowEffecterEnchantConfig {
        private BlindnessArrowConfig() {
            super(EnchantPlus.BLINDNESS_ARROW);
        }
    }

    public class ElectrocuteConfig extends WeaponEffecterEnchantConfig {
        private ElectrocuteConfig() {
            super(EnchantPlus.ELECTROCUTE);
        }

        public double getBaseIronDamage() {
            return get().getDouble(path("base-iron-damage"));
        }

        public double getExtraIronDamagePerLevel() {
            return get().getDouble(path("extra-iron-damage-per-level"));
        }

        public double getIronDamage(int level) {
            return getBaseIronDamage() + getExtraIronDamagePerLevel() * (level - 1);
        }

        public double getBaseGoldDamage() {
            return get().getDouble(path("base-gold-damage"));
        }

        public double getExtraGoldDamagePerLevel() {
            return get().getDouble(path("extra-gold-damage-per-level"));
        }

        public double getGoldDamage(int level) {
            return getBaseGoldDamage() + getExtraGoldDamagePerLevel() * (level - 1);
        }

        public double getBaseChainDamage() {
            return get().getDouble(path("base-chain-damage"));
        }

        public double getExtraChainDamagePerLevel() {
            return get().getDouble(path("extra-chain-damage-per-level"));
        }

        public double getChainDamage(int level) {
            return getBaseChainDamage() + getExtraChainDamagePerLevel() * (level - 1);
        }
    }

    public class ExcavationConfig extends EnchantConfig {
        private ExcavationConfig() {
            super(EnchantPlus.EXCAVATION);
        }

        public boolean isNoObsidian() {
            return get().getBoolean(path("no-obsidian"));
        }
    }

    public class ExplodingArrowConfig extends BowActivatableEnchantConfig {
        private ExplodingArrowConfig() {
            super(EnchantPlus.EXPLODING_ARROW);
        }

        public double getExplosionStrength(int level) {
            double strength = getBaseExplosionStrength();
            for (int i = 0; i < level; i++) {
                strength *= getStrengthRiseMultiplier();
            }
            return strength;
        }

        public double getBaseExplosionStrength() {
            return get().getDouble(path("base-explosion-strength"));
        }

        public boolean explodesBlocks() {
            return get().getBoolean(path("explodes-blocks"));
        }

        public double getStrengthRiseMultiplier() {
            return get().getDouble(path("strength-rise-multiplier"));
        }
    }

    public class UpdraftConfig extends ArmorEnchantConfig {
        private UpdraftConfig() {
            super(EnchantPlus.UPDRAFT);
        }

        public int getDurabilityDecay() {
            return get().getInt(path("durability-decay"));
        }
        
        public boolean disableWhenPvPStarts() {
            return get().getBoolean(path("disable-when-pvp-starts"));
        }

        public int getNoUpdraftPvPCooldown() {
            return get().getInt(path("no-updraft-pvp-cooldown"));
        }
        
        public boolean disableWhenPvEStarts() {
            return get().getBoolean(path("disable-when-pve-starts"));
        }
        
        public int getNoUpdraftPvECooldown() {
            return get().getInt(path("no-updraft-pve-cooldown"));
        }
        
        public boolean fallDamageUpdraft() {
            return get().getBoolean(path("falldamage-updraft"));
        }
    }

    public class HarvestingConfig extends EnchantConfig {
        private HarvestingConfig() {
            super(EnchantPlus.HARVESTING);
        }
    }

    public class HeavyCurseConfig extends EnchantConfig {
        private HeavyCurseConfig() {
            super(EnchantPlus.HEAVY_CURSE);
        }

        public boolean causesMiningFatigue() {
            return get().getBoolean(path("causes-mining-fatigue"));
        }
    }

    public class HungerCurseConfig extends ArmorEnchantConfig {
        private HungerCurseConfig() {
            super(EnchantPlus.HUNGER_CURSE);
        }

        public double getHungerRatePerLevel() {
            return get().getDouble(path("hunger-rate-per-level"));
        }
    }
    
    public class IceAspectConfig extends WeaponEffecterEnchantConfig {
        private IceAspectConfig() {
            super(EnchantPlus.ICE_ASPECT);
        }
    }

    public class InsulationConfig extends ArmorEnchantConfig {
        private InsulationConfig() {
            super(EnchantPlus.INSULATION);
        }

        public double getElectrocuteMultiplier() {
            return get().getDouble(path("electrocute-multiplier"));
        }

        public double getPlasmaMultiplier() {
            return get().getDouble(path("plasma-multiplier"));
        }

        public double getExtraDecrement(int level) {
            if (level <= 1) {
                return 0;
            } else {
                return get().getDouble(path("plasma-multiplier")) * (level - 1);
            }
        }
    }

    public class LifeLeechConfig extends WeaponActivatableEnchantConfig {
        private LifeLeechConfig() {
            super(EnchantPlus.LIFE_LEECH);
        }

        public int getLifeLeechHealAmount() {
            return get().getInt(path("life_leech-heal-amount"));
        }
    }

    public class LifeConfig extends ArmorEnchantConfig {
        private LifeConfig() {
            super(EnchantPlus.LIFE);
        }

        public int getHealthPerLevel() {
            return get().getInt(path("health-per-level"));
        }
    }

    public class RapidFireConfig extends BowEnchantConfig {
        private RapidFireConfig() {
            super(EnchantPlus.RAPID_FIRE);
        }

        public double getHealthPerLevel() {
            return get().getDouble(path("accuracy"));
        }
    }

    public class NightVisionConfig extends ArmorEnchantConfig {
        private NightVisionConfig() {
            super(EnchantPlus.NIGHT_VISION);
        }
    }

    public class PlasmaConfig extends WeaponActivatableEnchantConfig {
        private PlasmaConfig() {
            super(EnchantPlus.PLASMA);
        }

        public double getBaseLeatherDamage() {
            return get().getDouble(path("base-leather-damage"));
        }

        public double getExtraLeatherDamagePerLevel() {
            return get().getDouble(path("extra-leather-damage-per-level"));
        }

        public double getLeatherDamage(int level) {
            return getBaseLeatherDamage() + getExtraLeatherDamagePerLevel() * (level - 1);
        }

        public double getBaseDiamondDamage() {
            return get().getDouble(path("base-diamond-damage"));
        }

        public double getExtraDiamondDamagePerLevel() {
            return get().getDouble(path("extra-diamond-damage-per-level"));
        }

        public double getDiamondDamage(int level) {
            return getBaseDiamondDamage() + getExtraDiamondDamagePerLevel() * (level - 1);
        }
    }

    public class PoisonArrowConfig extends BowAmplifiedEffecterEnchantConfig {
        private PoisonArrowConfig() {
            super(EnchantPlus.POISON_ARROW);
        }
    }

    public class QuakeConfig extends WeaponEnchantConfig {
        private QuakeConfig() {
            super(EnchantPlus.QUAKE);
        }

        public boolean launchesPlayers() {
            return get().getBoolean(path("launches-players"));
        }

        public boolean launchesOtherEntities() {
            return get().getBoolean(path("launches-other-entities"));
        }

        public boolean doesFallDamageAffectPlayers() {
            return get().getBoolean(path("falldamage-affects-players"));
        }

        public double getFallDamage() {
            return get().getDouble(path("falldamage"));
        }
        
        public int getDurabilityCost() {
            return get().getInt(path("durability-cost"));
        }
        
        public boolean hasCooldown() {
            return get().getBoolean(path("has-cooldown"));
        }
        
        public int getCooldown() {
            return get().getInt(path("cooldown"));
        }
        
        public boolean prioritizesWorldInteraction() {
            return get().getBoolean(path("world-interaction-priority"));
        }
        
        public int getBaseExplosionSize() {
            return get().getInt(path("base-explosion-size"));
        }
        
        public int getExtraExplosionSizePerLevel() {
            return get().getInt(path("extra-explosion-size-per-level"));
        }
        
        public int getExplosionSize(int level) {
            return getBaseExplosionSize() + getExtraExplosionSizePerLevel() * (level - 1);
        }
        
        public double getBaseVelocity() {
            return get().getDouble(path("base-velocity"));
        }
        
        public double getExtraVelocityPerLevel() {
            return get().getDouble(path("extra-velocity-per-level"));
        }
        
        public double getVelocity(int level) {
            return getBaseVelocity() + getExtraExplosionSizePerLevel() * (level - 1);
        }
    }

    public class RegainConfig extends EnchantConfig {
        public RegainConfig() {
            super(EnchantPlus.REGAIN);
        }

        public List<Material> getDisabledMaterials() {
            List<String> materialNames = get().getStringList(path("disabled-materials"));
            List<Material> materials = new ArrayList<>();
            for (String name : materialNames) {
                try {
                    materials.add(Material.matchMaterial(name));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning(name + " is not exist on this version of minecraft.");
                }
            }
            return materials;
        }
    }

    public class RegenerationConfig extends ArmorEnchantConfig {
        public RegenerationConfig() {
            super(EnchantPlus.REGENERATION);
        }

        public double getHealRatePerLevel() {
            return get().getDouble(path("heal-rate-per-level"));
        }

        public int getHealAmount() {
            return get().getInt(path("heal-amount"));
        }
    }

    public class ReinforcedConfig extends EnchantConfig {
        public ReinforcedConfig() {
            super(EnchantPlus.REINFORCED);
        }

        public int getDurabilityCost() {
            return get().getInt(path("durability-cost"));
        }

        public double getBaseUnbreakingRate() {
            return get().getDouble(path("base-unbreaking-rate"));
        }
        
        public double getExtraUnbreakingRatePerLevel() {
            return get().getDouble(path("extra-unbreaking-rate-per-level"));
        }

        public double getUnbreakingRate(int level) {
            return getBaseUnbreakingRate() + getExtraUnbreakingRatePerLevel() * (level - 1);
        }
    }

    public class RejuvinationConfig extends ArmorEnchantConfig {
        public RejuvinationConfig() {
            super(EnchantPlus.REJUVINATION);
        }
        
        public double getBaseRegenRate() {
            return get().getDouble(path("base-regen-rate"));
        }

        public double getExtraRegenRatePerLevel() {
            return get().getDouble(path("extra-regen-rate-per-level"));
        }

        public double getRegenRate(int level) {
            return getBaseRegenRate() + getExtraRegenRatePerLevel() * (level - 1);
        }
    }

    public class SlownessConfig extends WeaponAmplifiedEffecterEnchantConfig {
        private SlownessConfig() {
            super(EnchantPlus.SLOWNESS);
        }
    }

    public class SoulboundConfig extends EnchantConfig {
        public SoulboundConfig() {
            super(EnchantPlus.SOULBOUND);
        }
        
        public boolean dissapearsAfterDeath() {
            return get().getBoolean(path("dissapears-after-death"));
        }
    }

    public class UnstableCurseConfig extends EnchantConfig {
        public UnstableCurseConfig() {
            super(EnchantPlus.UNSTABLE_CURSE);
        }
        
        public double getBaseDecayRate() {
            return get().getDouble(path("base-decay-rate"));
        }

        public double getExtraDecayRatePerLevel() {
            return get().getDouble(path("extra-decay-rate-per-level"));
        }

        public double getDecayRate(int level) {
            return getBaseDecayRate() + getExtraDecayRatePerLevel() * (level - 1);
        }
    }

    public class VenomConfig extends WeaponAmplifiedEffecterEnchantConfig {
        private VenomConfig() {
            super(EnchantPlus.VENOM);
        }
    }

    public class WaterBreathingConfig extends ArmorEnchantConfig {
        private WaterBreathingConfig() {
            super(EnchantPlus.WATER_BREATHING);
        }
    }

    public class WellFedConfig extends ArmorEnchantConfig {
        private WellFedConfig() {
            super(EnchantPlus.WELL_FED);
        }

        public double getWellFedRatePerLevel() {
            return get().getDouble(path("well-fed-rate-per-level"));
        }
    }

    public class WitheringArrowConfig extends BowAmplifiedEffecterEnchantConfig {
        private WitheringArrowConfig() {
            super(EnchantPlus.WITHERING_ARROW);
        }
    }

    public class WitheringConfig extends WeaponAmplifiedEffecterEnchantConfig {
        private WitheringConfig() {
            super(EnchantPlus.WITHERING);
        }
    }

    public class XPBoostConfig extends WeaponEnchantConfig {
        private XPBoostConfig() {
            super(EnchantPlus.XP_BOOST);
        }

        public double getBaseBoostMultiplier() {
            return get().getDouble(path("base-boost-multiplier"));
        }

        public double getExtraBoostMultiplierPerLevel() {
            return get().getDouble(path("extra-boost-multiplier-per-level"));
        }
        
        public double getBoostMultiplier(int level) {
            return getBaseBoostMultiplier() + getExtraBoostMultiplierPerLevel() * (level - 1);
        }
    }

}
