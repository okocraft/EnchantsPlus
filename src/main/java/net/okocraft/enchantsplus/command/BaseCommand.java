package net.okocraft.enchantsplus.command;

import java.util.Arrays;
import java.util.List;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.config.Languages;

import org.bukkit.command.CommandSender;

import lombok.Getter;

public abstract class BaseCommand {

    protected final Commands registration;
    protected final EnchantsPlus plugin;
    protected final Languages languages;

    @Getter
    protected final String name;
    @Getter
    protected final String permissionNode;
    @Getter
    protected final int leastArgLength;
    @Getter
    protected final boolean isPlayerOnly;
    @Getter
    protected final String usage;
    @Getter
    protected final List<String> alias;

    protected BaseCommand(Commands registration, String name, String permissionNode, int leastArgLength, boolean isPlayerOnly, String usage, String ... alias) {
        this.registration = registration;
        this.plugin = registration.plugin;
        this.languages = registration.languages;
        this.name = name;
        this.permissionNode = permissionNode;
        this.leastArgLength = leastArgLength;
        this.isPlayerOnly = isPlayerOnly;
        this.usage = usage;
        this.alias = Arrays.asList(alias);
    }

    public abstract boolean runCommand(CommandSender sender, String[] args);

    public List<String> runTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public boolean isValidArgsLength(int argsLength) {
        return getLeastArgLength() <= argsLength;
    }

    public boolean hasPermission(CommandSender sender) {
        if (permissionNode == null || permissionNode.isEmpty()) {
            return true;
        }

        return sender.hasPermission(getPermissionNode());
    }

    /**
     * numberを解析してint型にして返す。numberのフォーマットがintではないときはdefを返す。
     *
     * @param number 解析する文字列
     * @param def    解析に失敗したときに返す数字
     * @return int型の数字。
     * @author LazyGon
     * @since v1.1.0
     */
    protected int parseIntOrDefault(String number, int def) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            return def;
        }
    }
}