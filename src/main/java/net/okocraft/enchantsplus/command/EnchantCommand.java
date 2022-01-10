package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.okocraft.enchantsplus.config.Languages.Language;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.enchant.EnchantAPI.MaxEnchantCheckResult;
import net.okocraft.enchantsplus.event.EnchantingType;
import net.okocraft.enchantsplus.event.ItemCustomEnchantEvent;
import net.okocraft.enchantsplus.model.LocalItemStack;

import net.okocraft.enchantsplus.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.util.StringUtil;

public class EnchantCommand extends BaseCommand {

    EnchantCommand(Commands registration) {
        super(
            registration,
            "enchant",
            "enchantsplus.commands.enchant",
            1,
            false,
            "/ep <enchant|en> <Enchant name> [Enchant level] [Player]",
            "en"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Language language = languages.getLanguage(sender);
        
        List<EnchantPlus> enchantNames = plugin.getEnchantAPI().getEnabledEnchants();
        if (args.length == 1 || args[1].equalsIgnoreCase("list")) {
            language.command.enchantCommand.list.send(sender, enchantNames);
            return true;
        }

        EnchantPlus enchant = EnchantPlus.fromId(args[1]);
        if (enchant == null) {
            language.command.enchantNotFound.sendTo(sender);
            language.command.enchantCommand.list.send(sender, enchantNames);
            return true;
        }

        int level = 1;
        if (args.length >= 3) {
            try {
                level = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                language.command.enchantCommand.levelMustBeNumber.sendTo(sender);
                return true;
            }
        }

        boolean hasUnsafeEnchantPermission = sender.hasPermission("enchantsplus.commands.enchant.exceedmaxlevel");
        if (!hasUnsafeEnchantPermission && level > plugin.getMainConfig().getBy(enchant).getMaxLevel()) {
            language.command.exceedMaxLevel.sendTo(sender, level);
            return true;
        }

        Player player;
        if (args.length == 4) {
            player = Bukkit.getPlayer(args[3]);
            if (player == null) {
                language.command.noPlayerFound.sendTo(sender, args[3]);
                return true;
            }

            if (sender != player && !sender.hasPermission("enchantsplus.commands.enchant.others")) {
                language.command.notAllowedOthers.sendTo(sender);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                language.command.noPlayerDefined.sendTo(sender);
                return true;
            }

            player = (Player) sender;
        }

        LocalItemStack handItem = plugin.wrapItem(player.getEquipment().getItemInMainHand());
        if (handItem == null || !enchant.canEnchant(handItem.getType())) {
            language.command.invalidItem.sendTo(sender);
            return true;
        }

        HashMap<EnchantPlus, Integer> enchants = new HashMap<>();
        enchants.put(enchant, level);
        boolean isItemEnchantedBook = handItem.getType() == Material.ENCHANTED_BOOK;

        Config.GeneralConfig config = plugin.getMainConfig().getGeneralConfig();
        ItemCustomEnchantEvent event = new ItemCustomEnchantEvent(player, handItem, enchants, EnchantingType.COMMAND,
                (isItemEnchantedBook || !config.getAbsoluteMaxEnchantsIgnoresCommand()));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return true;
        }

        MaxEnchantCheckResult check = plugin.getEnchantAPI().checkMaxEnchantments(event);
        if (check == MaxEnchantCheckResult.TOO_MANY_TOTAL) {
            language.enchant.tooManyTotal.sendTo(event.getEnchanter(), config.getAbsoluteMaxEnchants());
            return true;
        } else if (check == MaxEnchantCheckResult.TOO_MANY_CUSTOM) {
            language.enchant.tooManyCustom.sendTo(event.getEnchanter(), config.getAbsoluteMaxEnchants());
            return true;
        } else if (check == MaxEnchantCheckResult.INVALID_ITEM) {
            // Other plugin changed item to invalid one.
            return true;
        }

        if (isItemEnchantedBook) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) handItem.getItemMeta();
            if (!meta.hasStoredEnchants()) {
                language.command.enchantCommand.noVanillaEnchantWarning.sendTo(sender);
            }
        }

        handItem.addCustomEnchant(enchant, level, isItemEnchantedBook || hasUnsafeEnchantPermission, true);
        player.getEquipment().setItemInMainHand(handItem.getItem());

        language.command.enchantCommand.enchantSuccess.sendTo(sender);
        return true;
    }
    
    @Override
    public List<String> runTabComplete(CommandSender sender, String[] args) {
        List<EnchantPlus> enchants = plugin.getEnchantAPI().getEnabledEnchants();
        List<String> completion = new ArrayList<>();
        if (args.length == 2) {
            completion.add("list");
            for (EnchantPlus enchant : enchants) {
                completion.add(enchant.getId());
            }
            return StringUtil.copyPartialMatches(args[1], completion, new ArrayList<>());
        }

        EnchantPlus picked = EnchantPlus.fromId(args[1]);
        if (picked == null) {
            return new ArrayList<>();
        }

        int maxLevel = plugin.getMainConfig().getBy(picked).getMaxLevel();
        if (args.length == 3) {
            for (int i = maxLevel; i >= 1; --i) {
                completion.add(String.valueOf(i));
            }
            return StringUtil.copyPartialMatches(args[2], completion, new ArrayList<>());
        }

        int pickedLevel;
        try {
            pickedLevel = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
        if (pickedLevel > maxLevel || !sender.hasPermission("enchantsplus.commands.enchant.others")) {
            return new ArrayList<>();
        }

        if (args.length == 4) {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (picked.canEnchant(online.getEquipment().getItemInMainHand().getType())) {
                    completion.add(online.getName());
                }
                return StringUtil.copyPartialMatches(args[3], completion, new ArrayList<>());
            }
        }

        return new ArrayList<>();
    }
}
