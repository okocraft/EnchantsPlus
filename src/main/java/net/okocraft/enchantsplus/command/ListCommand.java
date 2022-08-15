package net.okocraft.enchantsplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.enchant.EnchantmentTarget;

import net.okocraft.enchantsplus.config.Languages;
import org.bukkit.command.CommandSender;

public class ListCommand extends BaseCommand {

    protected ListCommand(Commands registration) {
        super(
            registration,
            "list",
            "enchantsplus.commands.list",
            1,
            false,
            "/ep list"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Languages.Language language = languages.getLanguage(sender);

        language.command.listCommand.line.sendWithoutPrefixTo(sender);

        EnumMap<EnchantmentTarget, List<EnchantPlus>> targetMap = new EnumMap<>(EnchantmentTarget.class);
        for (EnchantmentTarget target : EnchantmentTarget.values()) {
            for (EnchantPlus enchant : EnchantPlus.values()) {
                if (enchant.getTableTargets().contains(target)) {
                    targetMap.computeIfAbsent(target, t -> new ArrayList<>()).add(enchant);
                }
            }
        }

        List<EnchantmentTarget> targets = new ArrayList<>(Arrays.asList(EnchantmentTarget.values()));
        targets.retainAll(targetMap.keySet());
        targets.sort(Comparator.comparing(Enum::name));
        for (EnchantmentTarget target : targets) {
            language.command.listCommand.content.sendWithoutPrefixTo(sender, target, targetMap.get(target));
        }

        language.command.listCommand.line.sendWithoutPrefixTo(sender);
        
        return true;
    }
}
