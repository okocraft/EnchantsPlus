package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.okocraft.enchantsplus.config.PlayerData;

import net.okocraft.enchantsplus.config.Languages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ToggleCommand extends BaseCommand {
    
    protected ToggleCommand(Commands registration) {
        super(
            registration,
            "toggle",
            null,
            2,
            true,
            "/ep toggle <enchants | notifications | particles>"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Languages.Language language = languages.getLanguage(sender);
        Languages.Language.CommandSection.ToggleSection toggleCommand = language.command.toggleCommand;
        PlayerData data = plugin.getPlayerData();
        UUID uid = ((Player) sender).getUniqueId();
        boolean newValue;
        if (args[1].equalsIgnoreCase("enchants")) {
            if (!sender.hasPermission("enchantsplus.commands.toggle.enchants")) {
                language.command.noPermission.sendTo(sender, "enchantsplus.commands.toggle.enchants");
                return true;
            }

            newValue = data.toggleCustomEnchants(uid);

            if (newValue) {
                toggleCommand.customEnchantOn.sendTo(sender);
            } else {
                toggleCommand.customEnchantOff.sendTo(sender);
            }
        } else if (args[1].equalsIgnoreCase("notifications")) {
            if (!sender.hasPermission("enchantsplus.commands.toggle.notifications")) {
                language.command.noPermission.sendTo(sender, "enchantsplus.commands.toggle.notifications");
                return true;
            }
            // TODO: check main config and check if it is globally off.
            
            newValue = !data.getNotifications(uid);
            data.setNotifications(uid, newValue);            
            if (newValue) {
                toggleCommand.notificationsOn.sendTo(sender);
            } else {
                toggleCommand.notificationsOff.sendTo(sender);
            }
            
        } else if (args[1].equalsIgnoreCase("particles")) {
            if (!sender.hasPermission("enchantsplus.commands.toggle.particles")) {
                language.command.noPermission.sendTo(sender, "enchantsplus.commands.toggle.particles");
                return true;
            }
            // TODO: check main config and check if it is globally off.

            newValue = !data.getParticles(uid);
            data.setParticles(uid, newValue);            
            if (newValue) {
                toggleCommand.particlesOn.sendTo(sender);
            } else {
                toggleCommand.particlesOff.sendTo(sender);
            }
        }
        
        return true;
    }

    @Override
    public List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length == 2) {
            if (sender.hasPermission("enchantsplus.commands.toggle.enchants")) {
                completion.add("enchants");
            }
            if (sender.hasPermission("enchantsplus.commands.toggle.notifications")) {
                completion.add("notifications");
            }
            if (sender.hasPermission("enchantsplus.commands.toggle.particles")) {
                completion.add("particles");
            }

            return StringUtil.copyPartialMatches(args[1], completion, new ArrayList<>());
        }
        return completion;
    }
}
