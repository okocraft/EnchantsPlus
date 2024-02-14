package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Languages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.Nullable;

public class Commands implements CommandExecutor, TabCompleter {

    protected final EnchantsPlus plugin;
    protected final Languages languages;
    protected final Map<String, BaseCommand> registeredSubCommands = new LinkedHashMap<>();

    protected void register(BaseCommand subCommand) {
        String commandName = subCommand.getName().toLowerCase(Locale.ROOT);
        if (registeredSubCommands.containsKey(commandName)) {
            plugin.getLogger().warning("The command " + commandName + " is already registered.");
            return;
        }

        registeredSubCommands.put(commandName, subCommand);
    }

    public List<BaseCommand> getRegisteredCommands() {
        return new ArrayList<>(registeredSubCommands.values());
    }

    @Nullable
    public BaseCommand getSubCommand(String name) {
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
            if (subCommand.getAlias().contains(name.toLowerCase(Locale.ROOT))) {
                return subCommand;
            }
        }

        return null;
    }

    public Commands(EnchantsPlus plugin) {
        this.plugin = plugin;
        this.languages = plugin.getLanguagesConfig();
        PluginCommand pluginCommand = Objects.requireNonNull(plugin.getCommand("enchantsplus"), "The command 'enchantsplus' is not written in plugin.yml");
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);

        register(new HelpCommand(this));
        register(new EnchantCommand(this));
        register(new InfoCommand(this));
        register(new ToggleCommand(this));
        register(new ListCommand(this));
        register(new DeEnchantCommand(this));
        register(new ReloadCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        BaseCommand subCommand;
        Languages.Language language = languages.getLanguage(sender);
        Languages.Language.CommandSection command = language.command;
        
        if (args.length == 0) {
            command.noArgMessage.sendWithoutPrefixTo(sender, plugin.getVersion());
            return true;
        }
        
        if ((subCommand = getSubCommand(args[0])) == null) {
            command.noSuchCommand.sendTo(sender);
            return true;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            command.playerOnly.sendTo(sender);
            return false;
        }

        if (!subCommand.hasPermission(sender)) {
            command.noPermission.sendTo(sender, subCommand.getPermissionNode());
            return false;
        }

        if (!subCommand.isValidArgsLength(args.length)) {
            command.notEnoughArgument.sendTo(sender);
            command.usage.sendTo(sender, subCommand.getUsage());
            return false;
        }

        return subCommand.runCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> permittedCommands = getPermittedCommandNames(sender);
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], permittedCommands, new ArrayList<>());
        }

        if (!permittedCommands.contains(args[0].toLowerCase(Locale.ROOT))) {
            return List.of();
        }

        return getSubCommand(args[0]).runTabComplete(sender, args);
    }

    public List<BaseCommand> getPermittedCommands(CommandSender sender) {
        List<BaseCommand> result = new ArrayList<>();
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.hasPermission(sender)) {
                result.add(subCommand);
            }
        }
        return result;
    }

    private List<String> getPermittedCommandNames(CommandSender sender) {
        List<String> result = new ArrayList<>();
        for (BaseCommand subCommand : registeredSubCommands.values()) {
            if (subCommand.hasPermission(sender)) {
                result.add(subCommand.getName().toLowerCase(Locale.ROOT));
                result.addAll(subCommand.getAlias());
            }
        }
        return result;
    }
}