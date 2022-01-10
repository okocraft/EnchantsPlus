package net.okocraft.enchantsplus.command;

import java.util.List;

import net.okocraft.enchantsplus.config.Languages;
import org.bukkit.command.CommandSender;

public class HelpCommand extends BaseCommand {
    protected HelpCommand(Commands registration) {
        super(registration,
            "help",
            "enchantsplus.commands.help",
            1,
            false,
            "/ep help"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Languages.Language.CommandSection.HelpSection helpCommand = languages.getLanguage(sender).command.helpCommand;
        
        List<BaseCommand> permittedCommands = registration.getPermittedCommands(sender);
        if (permittedCommands.isEmpty()) {
            helpCommand.noPermittedCommand.sendTo(sender);
            return true;
        }
        
        helpCommand.line.sendWithoutPrefixTo(sender);
        for (BaseCommand command : permittedCommands) {
            helpCommand.content.sendWithoutPrefixTo(sender, command.usage, helpCommand.description.getHelpDescription(command));
        }
        helpCommand.line.sendWithoutPrefixTo(sender);
        return true;
    }
}
