package net.okocraft.enchantsplus.enchant.handler;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Config.HarvestingConfig;
import net.okocraft.enchantsplus.event.PlayerTickEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;
import net.okocraft.enchantsplus.util.NamespacedKeyManager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Harvesting extends TickDependentEnchantHandler<HarvestingConfig> {

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
        NamespacedKey fortuneKey = NamespacedKeyManager.HARVESTING_ORIGINAL_FORTUNE;
        NamespacedKey harvestingLevelKey = NamespacedKeyManager.HARVESTING_PREVIOUS_LEVEL;
        boolean hasFortuneKey = container.has(fortuneKey, PersistentDataType.INTEGER);
        int level = localHandItem.getCustomEnchantLevel(config.getType());

        // キーがあるがレベルがない -> エンチャントが削除された
        if (hasFortuneKey && level == 0) {
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            int originalFortune = container.getOrDefault(fortuneKey, PersistentDataType.INTEGER, 0);
            if (originalFortune != 0) {
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, originalFortune, false);
            }
            container.remove(fortuneKey);
            container.remove(harvestingLevelKey);
        
        // キーがないがレベルがある -> エンチャントが追加された
        } else if (!hasFortuneKey && level > 0) {
            int originalFortune = handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            container.set(fortuneKey, PersistentDataType.INTEGER, originalFortune);
            container.set(harvestingLevelKey, PersistentDataType.INTEGER, level);
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2 * level + originalFortune, true);
        
        // キーがあり、レベルもある -> エンチャントが付いた状態から変更なし。エンチャントの整合性を確認
        } else if (hasFortuneKey && level > 0) {
            int originalFortune = container.getOrDefault(fortuneKey, PersistentDataType.INTEGER, 0);
            int fortune = handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

            int previousHarvesting = container.getOrDefault(harvestingLevelKey, PersistentDataType.INTEGER, 0);

            // harvestingのレベルが変更された
            if (previousHarvesting != level) {
                container.set(harvestingLevelKey, PersistentDataType.INTEGER, level);
                meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2 * level + originalFortune, true);

            // 幸運のレベルが変更された
            } else if (2 * level + originalFortune != fortune) {
                originalFortune = fortune;
                container.set(fortuneKey, PersistentDataType.INTEGER, originalFortune);
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
