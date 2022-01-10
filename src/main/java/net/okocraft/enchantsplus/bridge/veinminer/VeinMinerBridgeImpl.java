package net.okocraft.enchantsplus.bridge.veinminer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wtf.choco.veinminer.VeinMiner;
import wtf.choco.veinminer.api.ActivationStrategy;
import wtf.choco.veinminer.data.AlgorithmConfig;
import wtf.choco.veinminer.data.PlayerPreferences;
import wtf.choco.veinminer.tool.ToolCategory;
import wtf.choco.veinminer.tool.ToolTemplate;
import wtf.choco.veinminer.utils.ItemValidator;
import wtf.choco.veinminer.utils.Pair;

public class VeinMinerBridgeImpl implements VeinMinerBridge {

    public VeinMinerBridgeImpl() {
        // Test to ensure plugin classes is on runtime.
        ToolCategory.getWithTemplate(new ItemStack(Material.STONE_PICKAXE));
    }

    @Override
    public boolean isVeinMining(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        Pair<@NotNull ToolCategory, @NotNull ToolTemplate> categoryTemplatePair = ToolCategory.getWithTemplate(item);
        ToolCategory category = categoryTemplatePair.getLeft();
        ToolTemplate toolTemplate = categoryTemplatePair.getRight();
        if (category == null || (category != ToolCategory.HAND && toolTemplate == null)) {
            return false;
        }

        // Invalid player state check
        PlayerPreferences playerData = PlayerPreferences.get(player);
        ActivationStrategy activation = playerData.getActivationStrategy();
        AlgorithmConfig algorithmConfig = (toolTemplate != null) ? toolTemplate.getConfig() : category.getConfig();
        return activation.isValid(player)
                && category.hasPermission(player)
                && !VeinMiner.getPlugin().getVeinMinerManager().isDisabledGameMode(player.getGameMode())
                && !playerData.isVeinMinerDisabled(category)
                && !algorithmConfig.isDisabledWorld(player.getWorld())
                && ItemValidator.isValid(item, category);
    }
}
