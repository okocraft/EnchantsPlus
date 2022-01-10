package net.okocraft.enchantsplus.command;

import net.okocraft.enchantsplus.config.Languages.Language;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

    protected ReloadCommand(Commands registration) {
        super(
            registration,
            "reload",
            "enchantsplus.commands.reload",
            1,
            false,
            "/ep reload"
        );
    }


    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Language language = languages.getLanguage(sender);
        language.command.reloadCommand.start.sendTo(sender);
        plugin.reload();
        language.command.reloadCommand.complete.sendTo(sender);
        return true;
    }
}
