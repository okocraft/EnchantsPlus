package net.okocraft.enchantsplus.enchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public enum EnchantPlus {

    AGILITY(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    AUTO_JUMP(
        false,
        Set.of(EnchantmentTarget.ARMOR_FEET),
        Set.of(EnchantmentTarget.ARMOR_FEET)
    ),
    AUTO_SMELT(
        false,
        Set.of(EnchantmentTarget.PICKAXE),
        Set.of(EnchantmentTarget.PICKAXE)
    ),
    AUTO_SPEED(
        false,
        Set.of(EnchantmentTarget.ARMOR_FEET),
        Set.of(EnchantmentTarget.ARMOR_FEET)
    ),
    BEASTMASTER(
        false,
        Set.of(EnchantmentTarget.ARMOR_HEAD),
        Set.of(EnchantmentTarget.ARMOR_HEAD)
    ),
    BEHEADING(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    BLINDNESS_ARROW(
        false,
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW),
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW)
    ),
    BLINDNESS(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    ELECTROCUTE(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    EXCAVATION(
        false,
        Set.of(EnchantmentTarget.TOOL),
        Set.of(EnchantmentTarget.TOOL)
    ),
    EXPLODING_ARROW(
        false,
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW),
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW)
    ),
    UPDRAFT(
        false,
        Set.of(EnchantmentTarget.ELYTRA),
        Set.of(EnchantmentTarget.ELYTRA)
    ),
    HARVESTING(
        false,
        Set.of(EnchantmentTarget.HOE),
        Set.of(EnchantmentTarget.HOE)
    ),
    HEAVY_CURSE(
        true,
        Set.of(EnchantmentTarget.ALL),
        Set.of(EnchantmentTarget.ALL)
    ),
    HUNGER_CURSE(
        true,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    ICE_ASPECT(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD)
    ),
    INSULATION(
        false,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    LIFE_LEECH(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    LIFE(
        false,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    NIGHT_VISION(
        false,
        Set.of(EnchantmentTarget.ARMOR_HEAD),
        Set.of(EnchantmentTarget.ARMOR_HEAD)
    ),
    PLASMA(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    POISON_ARROW(
        false,
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW),
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW)
    ),
    QUAKE(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    RAPID_FIRE(
        false,
        Set.of(EnchantmentTarget.BOW),
        Set.of(EnchantmentTarget.BOW)
    ),
    REGAIN(
        false,
        Set.of(EnchantmentTarget.TOOL),
        Set.of(EnchantmentTarget.TOOL)
    ),
    REGENERATION(
        false,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    REINFORCED(
        false,
        Set.of(EnchantmentTarget.PICKAXE),
        Set.of(EnchantmentTarget.PICKAXE)
    ),
    REJUVINATION(
        false,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    SLOWNESS(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    SOULBOUND(
        false,
        Set.of(EnchantmentTarget.ALL),
        Set.of(EnchantmentTarget.ALL)
    ),
    UNSTABLE_CURSE(
        true,
        Set.of(EnchantmentTarget.BREAKABLE),
        Set.of(EnchantmentTarget.BREAKABLE)
    ),
    VENOM(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    WATER_BREATHING(
        false,
        Set.of(EnchantmentTarget.ARMOR_HEAD),
        Set.of(EnchantmentTarget.ARMOR_HEAD)
    ),
    WELL_FED(
        false,
        Set.of(EnchantmentTarget.ARMOR),
        Set.of(EnchantmentTarget.ARMOR)
    ),
    WITHERING_ARROW(
        false,
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW),
        Set.of(EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW)
    ),
    WITHERING(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    ),
    XP_BOOST(
        false,
        Set.of(EnchantmentTarget.SWORD),
        Set.of(EnchantmentTarget.SWORD, EnchantmentTarget.AXE)
    );

    @Getter
    public final boolean treasure;
    @Getter
    public final boolean cursed;
    public final Set<EnchantmentTarget> tableTargets;
    public final Set<EnchantmentTarget> anvilTargets;
    
    /**
     * For treasure enchants.
     * 
     * @param cursed if the enchant is cursed.
     * @param anvilTargets appricable targets to conbine with anvil.
     */
    EnchantPlus(boolean cursed, Set<EnchantmentTarget> anvilTargets) {
        this.treasure = true;
        this.cursed = cursed;
        this.tableTargets = Set.of();
        this.anvilTargets = anvilTargets;
    }
    
    /**
     * For non-treasure enchants.
     * 
     * @param cursed if the enchant is cursed.
     * @param tableTargets appricable targets to enchant with enchanting table.
     * @param anvilTargets appricable targets to conbine with anvil.
     */
    EnchantPlus(boolean cursed, Set<EnchantmentTarget> tableTargets, Set<EnchantmentTarget> anvilTargets) {
        this.treasure = false;
        this.cursed = cursed;
        this.tableTargets = tableTargets;
        this.anvilTargets = anvilTargets;
    }

    public Set<EnchantmentTarget> getTableTargets() {
        return new HashSet<>(tableTargets);
    }

    public Set<EnchantmentTarget> getAnvilTargets() {
        return new HashSet<>(anvilTargets);
    }

    public boolean canEnchant(Material item) {
        if (item == null) {
            return false;
        } else if (!treasure && item == Material.ENCHANTED_BOOK) {
            return true;
        } else {
            for (EnchantmentTarget target : tableTargets) {
                if (target.includes(item)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean canEnchantWithAnvil(Material item) {
        if (item == null) {
            return false;
        } else if (item == Material.ENCHANTED_BOOK) {
            return true;
        } else {
            for (EnchantmentTarget target : anvilTargets) {
                if (target.includes(item)) {
                    return true;
                }
            }
            return false;
        }
    }

    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean conflictsWith(EnchantPlus enchant) {
        return ConflictionManager.conflictsWith(this, enchant);
    }

    public boolean conflictsWith(Enchantment enchant) {
        return ConflictionManager.conflictsWith(this, enchant);
    }

    public boolean conflictsWith(Set<EnchantPlus> enchantsCustom, Set<Enchantment> enchantsBukkit) {
        for (EnchantPlus enchantCustom : enchantsCustom) {
            if (conflictsWith(enchantCustom)) {
                return true;
            }
        }

        for (Enchantment enchantBukkit : enchantsBukkit) {
            if (conflictsWith(enchantBukkit)) {
                return true;
            }
        }

        return false;
    }

    public static Set<EnchantPlus> getApplicableEnchantsFromTable(Material item) {
        Set<EnchantPlus> result = new HashSet<>();
        for (EnchantPlus enchant : values()) {
            if (enchant.canEnchant(item)) {
                result.add(enchant);
            }
        }
        return result;
    }

    public static Set<EnchantPlus> getApplicableEnchantsFromAnvil(Material item) {
        Set<EnchantPlus> result = new HashSet<>();
        for (EnchantPlus enchant : values()) {
            if (enchant.canEnchantWithAnvil(item)) {
                result.add(enchant);
            }
        }
        return result;
    }

    public static EnchantPlus fromId(String id) {
        try {
            return valueOf(id.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static List<String> ids;
    public static List<String> getIds() {
        if (ids == null) {
            ids = Collections.synchronizedList(new ArrayList<>());
        }
        if (ids.isEmpty()) {
            for (EnchantPlus enchant : values()) {
                ids.add(enchant.getId());
            }
        }
        return new ArrayList<>(ids);
    }
}
