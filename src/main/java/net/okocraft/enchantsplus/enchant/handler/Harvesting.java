package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.HarvestingConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Harvesting extends TickDependentEnchantHandler<HarvestingConfig> {

    private static final NamespacedKey HARVESTING_ORIGINAL_FORTUNE = new NamespacedKey("enchantsplus", "original_fortune");
    private static final NamespacedKey HARVESTING_PREVIOUS_LEVEL = new NamespacedKey("enchantsplus", "harvesting_previous_level");

    public Harvesting(EnchantsPlus plugin) {
        super(plugin, plugin.getMainConfig().getHarvestingConfig());
    }

    @Override
    public Class<PlayerTickEvent> getEventClass() {
        return PlayerTickEvent.class;
    }

    @Override
    public void handle(PlayerTickEvent event) {
        Player player = event.getPlayer();
        if (!config.isEnabled() || !checkTickInterval(player, 20)
                || isDisabledByWorldGuard(player, player.getLocation())) {
            return;
        }

        ItemStack handItem = player.getEquipment().getItemInMainHand();
        LocalItemStack localHandItem = plugin.wrapItem(handItem);
        if (localHandItem == null || localHandItem.getType() == Material.ENCHANTED_BOOK) {
            return;
        }

        ItemMeta meta = handItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        boolean hasFortuneKey = container.has(HARVESTING_ORIGINAL_FORTUNE, PersistentDataType.INTEGER);
        int level = localHandItem.getCustomEnchantLevel(config.getType());

        // キーがあるがレベルがない -> エンチャントが削除された
        if (hasFortuneKey && level == 0) {
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            int originalFortune = container.getOrDefault(HARVESTING_ORIGINAL_FORTUNE, PersistentDataType.INTEGER, 0);
            if (originalFortune != 0) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, originalFortune, false);
            }
            container.remove(HARVESTING_ORIGINAL_FORTUNE);
            container.remove(HARVESTING_PREVIOUS_LEVEL);
        
        // キーがないがレベルがある -> エンチャントが追加された
        } else if (!hasFortuneKey && level > 0) {
            int originalFortune = handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            container.set(HARVESTING_ORIGINAL_FORTUNE, PersistentDataType.INTEGER, originalFortune);
            container.set(HARVESTING_PREVIOUS_LEVEL, PersistentDataType.INTEGER, level);
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2 * level + originalFortune, true);
        
        // キーがあり、レベルもある -> エンチャントが付いた状態から変更なし。エンチャントの整合性を確認
        } else if (hasFortuneKey && level > 0) {
            int originalFortune = container.getOrDefault(HARVESTING_ORIGINAL_FORTUNE, PersistentDataType.INTEGER, 0);
            int fortune = handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

            int previousHarvesting = container.getOrDefault(HARVESTING_PREVIOUS_LEVEL, PersistentDataType.INTEGER, 0);

            // harvestingのレベルが変更された
            if (previousHarvesting != level) {
                container.set(HARVESTING_PREVIOUS_LEVEL, PersistentDataType.INTEGER, level);
                meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2 * level + originalFortune, true);

            // 幸運のレベルが変更された
            } else if (2 * level + originalFortune != fortune) {
                originalFortune = fortune;
                container.set(HARVESTING_ORIGINAL_FORTUNE, PersistentDataType.INTEGER, originalFortune);
                meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2 * level + originalFortune, true);
            }

        // キーもレベルもない -> エンチャントが付けられていない。
        } else if (!hasFortuneKey && level == 0) {
            return;
        }

        handItem.setItemMeta(meta);
        player.getEquipment().setItemInMainHand(handItem);
    }
}
