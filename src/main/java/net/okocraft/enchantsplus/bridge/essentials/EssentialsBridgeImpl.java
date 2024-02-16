package net.okocraft.enchantsplus.bridge.essentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.Commanditemlore;
import net.ess3.api.IEssentials;
import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.model.LocalItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class EssentialsBridgeImpl implements EssentialsBridge, Listener {

    private static final Set<String> LORE_COMMANDS;

    static {
        LORE_COMMANDS =
                Stream.of("itemlore", "lore", "elore", "ilore", "eilore", "eitemlore")
                        .flatMap(command -> Stream.of("/" + command, "/essentials:" + command))
                        .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void registerTabCompletionListener(@NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregisterTabCompletionListener() {
        HandlerList.unregisterAll(this);
    }


    @EventHandler
    public void onTabCompletion(@NotNull TabCompleteEvent event) {
        String buffer = event.getBuffer();

        if (buffer.isEmpty() || buffer.charAt(0) != '/') {
            return;
        }

        int firstBlank = buffer.indexOf(" ");
        String command = buffer.substring(0, firstBlank != -1 ? firstBlank : buffer.length());

        if (!LORE_COMMANDS.contains(command)) {
            return;
        }

        String[] args = buffer.substring(firstBlank + 1).split(" ", 2);

        if (args.length < 2 || !args[0].equalsIgnoreCase("set") || !(event.getSender() instanceof Player player)) {
            return;
        }

        LocalItemStack handItem = EnchantsPlus.getInstance().wrapItem(player.getInventory().getItemInMainHand());

        if (handItem == null) {
            return;
        }

        if (args.length == 2) {
            List<String> completion = IntStream.rangeClosed(1, handItem.calculateOriginalLoreLines()).mapToObj(Integer::toString).toList();
            event.setCompletions(StringUtil.copyPartialMatches(args[1], completion, new ArrayList<>()));
        }

        int loreLineIndex;

        try {
            loreLineIndex = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        int enchantLoreLines = handItem.calculateEnchantLoreLines();
        args[1] = String.valueOf(loreLineIndex + enchantLoreLines);
        IEssentials plugin = Essentials.getPlugin(Essentials.class);
        event.setCompletions(new Commanditemlore().tabComplete(plugin.getServer(), plugin.getUser(player), null, null, args));
    }
}
