package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.AutoSmeltConfig;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AutoSmelt extends EnchantPlusHandler<AutoSmeltConfig, BlockDropItemEvent> {

    private final Map<Material, FurnaceResult> resultMap = new HashMap<>();

    public AutoSmelt(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getAutoSmeltConfig());
        collectFurnaceRecipes();
    }

    @Override
    public Class<BlockDropItemEvent> getEventClass() {
        return BlockDropItemEvent.class;
    }

    @Override
    public void handle(BlockDropItemEvent event) {
        if (event.isCancelled() || !config.isEnabled()) {
            return;
        }

        Player user = event.getPlayer();

        if (!Tag.MINEABLE_PICKAXE.isTagged(event.getBlockState().getType())) {
            return;
        }

        if (event.getBlockState() instanceof Container) {
            return;
        }

        if (event.getBlockState() instanceof Container) {
            return;
        }

        LocalItemStack handItem = plugin.wrapItem(user.getInventory().getItemInMainHand());

        if (!isValidTool(handItem) ||
                isDisabledByWorldGuard(user, event.getBlock().getLocation()) ||
                handItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
            return;
        }

        int exp = 0;
        boolean smelted = false;
        for (Item dropItem : event.getItems()) {
            ItemStack drop = dropItem.getItemStack();
            var result = resultMap.get(drop.getType());
            if (result != null) {
                exp += result.experience();
                dropItem.setItemStack(new ItemStack(result.type(), drop.getAmount()));
                smelted = true;
            }
        }

        if (!smelted) {
            return;
        }

        int finalExp = exp;

        Location location = event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D);
        World world = event.getBlock().getWorld();

        world.spawn(location, ExperienceOrb.class, orb -> orb.setExperience(finalExp));

        if (generalConfig.isParticlesEnabled()) {
            world.spawnParticle(Particle.FLAME, location, 5, 0.5F, 0.5F, 0.5F, 0.05F);
        }

        if (generalConfig.isSoundsEnabled()) {
            float soundVolume = generalConfig.getSoundVolume();
            if (soundVolume > 0.15F) {
                soundVolume -= 0.15F;
            }
            world.playSound(location, Sound.ENTITY_GUARDIAN_ATTACK, soundVolume, 0.8F);
        }
    }

    private void collectFurnaceRecipes() {
        Bukkit.recipeIterator().forEachRemaining(this::processRecipe);
    }

    private void processRecipe(@NotNull Recipe recipe) {
        if (recipe instanceof FurnaceRecipe furnaceRecipe &&
                furnaceRecipe.getInputChoice() instanceof RecipeChoice.MaterialChoice choice) {
            var furnaceResult = new FurnaceResult(recipe.getResult().getType(), furnaceRecipe.getExperience());
            choice.getChoices().forEach(material -> resultMap.put(material, furnaceResult));
        }
    }

    private record FurnaceResult(@NotNull Material type, float experience) {
    }
}
