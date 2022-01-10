package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;

import net.okocraft.enchantsplus.config.Languages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class DeEnchantCommand extends BaseCommand {

    protected DeEnchantCommand(Commands registration) {
        super(
            registration,
            "deenchant",
            "enchantsplus.commands.deenchant",
            1,
            false,
            "/ep deenchant [enchant name] [player]",
            "den"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Languages.Language language = languages.getLanguage(sender);

        Player target;
        LocalItemStack handItem;
        Set<EnchantPlus> removal = new HashSet<>();

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                language.command.playerOnly.sendTo(sender);
                return true;
            }

            target = (Player) sender;
            handItem = plugin.wrapItem(target.getEquipment().getItemInMainHand());
            if (handItem == null) {
                language.command.invalidItem.sendTo(sender);
                return true;
            }
            if (handItem.getCustomEnchants().isEmpty()) {
                language.command.enchantNotFound.sendTo(sender);
                return true;
            }
            removal.addAll(Arrays.asList(EnchantPlus.values()));

        } else if (args.length == 2) {
            if (!(sender instanceof Player)) {
                language.command.playerOnly.sendTo(sender);
                return true;
            }

            target = (Player) sender;
            handItem = plugin.wrapItem(target.getEquipment().getItemInMainHand());
            if (handItem == null) {
                language.command.invalidItem.sendTo(sender);
                return true;
            }
            if (handItem.getCustomEnchants().isEmpty()) {
                language.command.enchantNotFound.sendTo(sender);
                return true;
            }
            EnchantPlus enchant = EnchantPlus.fromId(args[1].toLowerCase());
            if (enchant == null) {
                if (args[1].equalsIgnoreCase("all")) {
                    removal.addAll(Arrays.asList(EnchantPlus.values()));
                } else {
                    language.command.enchantNotFound.sendTo(sender);
                    List<EnchantPlus> enchants = new ArrayList<>(handItem.getCustomEnchants().keySet());
                    language.command.enchantCommand.list.send(sender, enchants);
                    return true;
                }
            } else {
                removal.add(enchant);
            }

        } else {
            target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                language.command.noPlayerFound.sendTo(sender, args[2]);
                return true;
            }
            if (target != sender && !sender.hasPermission("enchantsplus.commands.deenchant.others")) {
                language.command.notAllowedOthers.sendTo(sender);
                return true;
            }
            handItem = plugin.wrapItem(target.getEquipment().getItemInMainHand());
            if (handItem == null) {
                language.command.invalidItem.sendTo(sender);
                return true;
            }
            if (handItem.getCustomEnchants().isEmpty()) {
                language.command.enchantNotFound.sendTo(sender);
                return true;
            }
            
            EnchantPlus enchant = EnchantPlus.fromId(args[1].toLowerCase());
            if (enchant == null) {
                if (args[1].equalsIgnoreCase("all")) {
                    removal.addAll(Arrays.asList(EnchantPlus.values()));
                } else {
                    language.command.enchantNotFound.sendTo(sender);
                    List<EnchantPlus> enchants = new ArrayList<>(handItem.getCustomEnchants().keySet());
                    language.command.enchantCommand.list.send(sender, enchants);
                    return true;
                }
            } else {
                removal.add(enchant);
            }
        }

        boolean modified = false;
        for (EnchantPlus enchant : removal) {
            if (handItem.removeCustomEnchant(enchant) != 0) {
                modified = true;
            }
        }
        if (modified) {
            target.getInventory().setItemInMainHand(handItem.getItem());
            language.command.deenchantCommand.deenchantSuccess.sendTo(sender);
        } else {
            language.command.deenchantCommand.itemNotChanged.sendTo(sender);
        }
        return true;
    }
    
    @Override
    public List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> completion = new ArrayList<>();

        if (args.length == 2) {
            completion.add("all");
            for (EnchantPlus enchant : EnchantPlus.values()) {
                completion.add(enchant.getId());
            }
            return StringUtil.copyPartialMatches(args[1], completion, new ArrayList<>());
        }

        if (EnchantPlus.fromId(args[1]) == null || !args[1].equalsIgnoreCase("all")) {
            return new ArrayList<>();
        }

        if (args.length == 3 && sender.hasPermission("enchantsplus.commands.deenchant.others")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                completion.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[2], completion, new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
