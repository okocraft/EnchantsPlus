package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.List;

import net.okocraft.enchantsplus.enchant.EnchantPlus;

import net.okocraft.enchantsplus.config.Languages;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class InfoCommand extends BaseCommand {

    protected InfoCommand(Commands registration) {
        super(
            registration,
            "info",
            "enchantsplus.commands.info",
            2,
            false,
            "/ep info <enchant name>"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Languages.Language language = languages.getLanguage(sender);

        EnchantPlus enchant = EnchantPlus.fromId(args[1]);
        if (enchant == null) {
            language.command.enchantNotFound.sendWithoutPrefixTo(sender);
        } else {
            language.enchantInfo.defaultFormat.sendWithoutPrefixTo(sender, enchant);
        }
        return true;
    }

    @Override
    public List<String> runTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], EnchantPlus.getIds(), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
