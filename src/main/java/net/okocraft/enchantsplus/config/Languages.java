package net.okocraft.enchantsplus.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.okocraft.enchantsplus.EnchantsPlus;
import net.okocraft.enchantsplus.command.BaseCommand;
import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.enchant.EnchantmentTarget;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import lombok.Getter;

public final class Languages {

    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList("en_us", "ja_jp"));

    private final EnchantsPlus plugin;
    private final Supplier<String> defaultLanguage;

    private final Map<String, Language> languages = new HashMap<>();

    public Languages(EnchantsPlus plugin) {
        this.plugin = plugin;
        this.defaultLanguage = () -> plugin.getMainConfig().getGeneralConfig().getDefaultLanguage();
        SUPPORTED_LANGUAGES.forEach(this::getLanguage);
    }

    public class Language extends CustomConfig {

        @Getter
        private final String languageCode;

        Language(String languageCode) {
            super(Languages.this.plugin, "language/" + languageCode + ".yml");
            this.languageCode = languageCode;
        }

        public final CommandSection command = new CommandSection();
        public class CommandSection extends Section {
            private CommandSection() {
                super("command");
            }

            public final NoPlaceholderEndPoint playerOnly = new NoPlaceholderEndPoint(this, "player-only");
            public final NoPlaceholderEndPoint notEnoughArgument = new NoPlaceholderEndPoint(this, "not-enough-argument");
            public final NoPlaceholderEndPoint noSuchCommand = new NoPlaceholderEndPoint(this, "no-such-command");
            public final NoPlaceholderEndPoint enchantNotFound = new NoPlaceholderEndPoint(this, "no-enchant-found");
            public final NoPlaceholderEndPoint notAllowedOthers = new NoPlaceholderEndPoint(this, "not-allowed-others");
            public final NoPlaceholderEndPoint noPlayerDefined = new NoPlaceholderEndPoint(this, "no-player-defined");
            public final NoPlaceholderEndPoint invalidItem = new NoPlaceholderEndPoint(this, "invalid-item");
            public final PlaceholderEndPoint<Integer> exceedMaxLevel = new PlaceholderEndPoint<>(this, "exceed-max-level", "%level%");
            public final PlaceholderEndPoint<String> noPermission = new PlaceholderEndPoint<>(this, "no-permission", "%permission%");
            public final PlaceholderEndPoint<String> usage = new PlaceholderEndPoint<>(this, "usage", "%usage%");
            public final PlaceholderEndPoint<String> noArgMessage = new PlaceholderEndPoint<>(this, "no-arg-message", "%version%");
            public final PlaceholderEndPoint<String> noPlayerFound = new PlaceholderEndPoint<>(this, "no-player-found", "%name%");

            public final EnchantSection enchantCommand = new EnchantSection();
            public class EnchantSection extends Section {
                private EnchantSection() {
                    super(CommandSection.this, "enchant-command");
                }
                public final NoPlaceholderEndPoint levelMustBeNumber = new NoPlaceholderEndPoint(this, "level-must-be-number");
                public final NoPlaceholderEndPoint noVanillaEnchantWarning = new NoPlaceholderEndPoint(this, "no-vanilla-enchant-warning");
                public final NoPlaceholderEndPoint enchantSuccess = new NoPlaceholderEndPoint(this, "enchant-success");

                public final List list = new List();
                public class List extends EndPoint {
                    private List() {
                        super(EnchantSection.this, "list");
                    }

                    public void send(CommandSender sender, java.util.List<EnchantPlus> enchants) {
                        sendMessage(sender, placeholder("%enchants%", enchants.toString()));
                    }
                }
            }

            public final VerboseSection verboseCommand = new VerboseSection();
            public class VerboseSection extends Section {
                private VerboseSection() {
                    super(CommandSection.this, "verbose-command");
                }

                public final NoPlaceholderEndPoint checkEnabling = new NoPlaceholderEndPoint(this, "check-enabling");
                public final NoPlaceholderEndPoint checkDisabling = new NoPlaceholderEndPoint(this, "check-disabling");
                public final NoPlaceholderEndPoint checkEnd = new NoPlaceholderEndPoint(this, "check-end");

            }

            public final HelpSection helpCommand = new HelpSection();
            public class HelpSection extends Section {
                private HelpSection() {
                    super(CommandSection.this, "help-command");
                }

                public final NoPlaceholderEndPoint noPermittedCommand = new NoPlaceholderEndPoint(this, "no_permitted-command");
                public final NoPlaceholderEndPoint line = new NoPlaceholderEndPoint(this, "line");
                public final BiPlaceholderEndPoint<String, String> content = new BiPlaceholderEndPoint<>(this, "content", "%usage%", "%description%");

                public final Description description = new Description();
                public class Description extends Section {
                    private final Map<String, NoPlaceholderEndPoint> index = new HashMap<>();

                    private Description() {
                        super(HelpSection.this, "description");

                        ConfigurationSection configurationSection = get().getConfigurationSection(getPath());
                        if (configurationSection == null) {
                            return;
                        }

                        for (String key : configurationSection.getKeys(false)) {
                            if (configurationSection.isString(key)) {
                                index.put(key.replaceAll("-", ""), new NoPlaceholderEndPoint(this, key));
                            }
                        }
                    }

                    public String getHelpDescription(BaseCommand command) {
                        NoPlaceholderEndPoint description = index.get(command.getName());
                        if (description != null) {
                            return description.getColorCodesTranslatedValue();
                        } else {
                            return "";
                        }
                    }
                }
            }

            public final ToggleSection toggleCommand = new ToggleSection();
            public class ToggleSection extends Section {
                private ToggleSection() {
                    super(CommandSection.this, "toggle-command");
                }

                public final NoPlaceholderEndPoint customEnchantOn = new NoPlaceholderEndPoint(this, "custom-enchant-on");
                public final NoPlaceholderEndPoint customEnchantOff = new NoPlaceholderEndPoint(this, "custom-enchant-off");
                public final NoPlaceholderEndPoint notificationsOn = new NoPlaceholderEndPoint(this, "notifications-on");
                public final NoPlaceholderEndPoint notificationsOff = new NoPlaceholderEndPoint(this, "notifications-off");
                public final NoPlaceholderEndPoint notificationsGloballyOff = new NoPlaceholderEndPoint(this, "notifications-globally-off");
                public final NoPlaceholderEndPoint particlesOn = new NoPlaceholderEndPoint(this, "particles-on");
                public final NoPlaceholderEndPoint particlesOff = new NoPlaceholderEndPoint(this, "particles-off");
                public final NoPlaceholderEndPoint particlesGloballyOff = new NoPlaceholderEndPoint(this, "particles-globally-off");
            }

            public final ListSection listCommand = new ListSection();
            public class ListSection extends Section {
                private ListSection() {
                    super(CommandSection.this, "list-command");
                }

                public final NoPlaceholderEndPoint line = new NoPlaceholderEndPoint(this, "line");

                public final Content content = new Content();
                public class Content extends EndPoint {
                    private Content() {
                        super(ListSection.this, "content");
                    }

                    public void sendWithoutPrefixTo(CommandSender sender, EnchantmentTarget target, java.util.List<EnchantPlus> enchants) {
                        StringBuilder builder = new StringBuilder("&f[&r");
                        for (int i = 0; i < enchants.size(); i++) {
                            EnchantPlus enchant = enchants.get(i);
                            Config.EnchantConfig config = plugin.getMainConfig().getBy(enchant);

                            if (!config.isEnabled()) {
                                builder.append("&c");
                            } else if (enchant.isCursed()) {
                                builder.append("&6");
                            } else {
                                builder.append("&a");
                            }

                            builder.append(config.getDisplayName()).append("&r");

                            if (i + 1 < enchants.size()) {
                                builder.append("&7, &r");
                            }
                        }

                        builder.append("&f]");

                        sendMessage(sender, false, placeholder("%enchant-target%", target.getName(), "%enchants%", builder.toString()));
                    }
                }
            }

            public final ReloadSection reloadCommand = new ReloadSection();
            public class ReloadSection extends Section {
                private ReloadSection() {
                    super(CommandSection.this, "reload-command");
                }

                public final NoPlaceholderEndPoint start = new NoPlaceholderEndPoint(this, "start");
                public final NoPlaceholderEndPoint complete = new NoPlaceholderEndPoint(this, "complete");
            }

            public final DeEnchantSection deenchantCommand = new DeEnchantSection();
            public class DeEnchantSection extends Section {
                private DeEnchantSection() {
                    super(CommandSection.this, "deenchant-command");
                }

                public final NoPlaceholderEndPoint deenchantSuccess = new NoPlaceholderEndPoint(this, "deenchant-success");
                public final NoPlaceholderEndPoint itemNotChanged = new NoPlaceholderEndPoint(this, "item-not-changed");
            }
        }

        public final ShopSection shop = new ShopSection();
        public class ShopSection extends Section {
            private ShopSection() {
                super("shop");
            }

            public final PlaceholderEndPoint<String> enchNotFound1 = new PlaceholderEndPoint<>(this, "ench_not_found_1", "%enchant%");
            public final NoPlaceholderEndPoint enchNotFound2 = new NoPlaceholderEndPoint(this, "ench_not_found_2");
            public final PlaceholderEndPoint<String> wrongLevelFormat = new PlaceholderEndPoint<>(this, "wrong_level_format", "%input%");
            public final NoPlaceholderEndPoint wrongLevel = new NoPlaceholderEndPoint(this, "wrong_level");
            public final NoPlaceholderEndPoint wrongItem = new NoPlaceholderEndPoint(this, "wrong_item");
            public final PlaceholderEndPoint<String> wrongPriceFormat = new PlaceholderEndPoint<>(this, "wrong_price_format", "%input%");
            public final NoPlaceholderEndPoint itemAlreadyHasEnchant = new NoPlaceholderEndPoint(this, "item_already_has_enchant");
            public final NoPlaceholderEndPoint notEnoughMoney = new NoPlaceholderEndPoint(this, "not_enough_money");
            public final NoPlaceholderEndPoint notEnoughPoints = new NoPlaceholderEndPoint(this, "not_enough_points");
            public final NoPlaceholderEndPoint playerpointsNotLoaded = new NoPlaceholderEndPoint(this, "playerpoints_not_loaded");
            public final NoPlaceholderEndPoint vaultNotLoaded = new NoPlaceholderEndPoint(this, "vault_not_loaded");
            public final NoPlaceholderEndPoint purchased = new NoPlaceholderEndPoint(this, "purchased");
            public final NoPlaceholderEndPoint noVanillaEnchants = new NoPlaceholderEndPoint(this, "no_vanilla_enchants");
            public final NoPlaceholderEndPoint cannotCreateShopNoVault = new NoPlaceholderEndPoint(this, "cannot_create_shop_no_vault");
            public final NoPlaceholderEndPoint signBroken = new NoPlaceholderEndPoint(this, "sign_broken");
        }

        public final EnchantSection enchant = new EnchantSection();
        public class EnchantSection extends Section {
            private EnchantSection() {
                super("enchant");
            }

            public final NoPlaceholderEndPoint electrocuteConcussion = new NoPlaceholderEndPoint(this, "electrocute-concussion");
            public final NoPlaceholderEndPoint frozen = new NoPlaceholderEndPoint(this, "frozen");
            public final NoPlaceholderEndPoint poisoned = new NoPlaceholderEndPoint(this, "poisoned");
            public final NoPlaceholderEndPoint blinded = new NoPlaceholderEndPoint(this, "blinded");
            public final NoPlaceholderEndPoint slowed = new NoPlaceholderEndPoint(this, "slowed");
            public final NoPlaceholderEndPoint withered = new NoPlaceholderEndPoint(this, "withered");
            public final PlaceholderEndPoint<Double> leeched = new PlaceholderEndPoint<>(this, "leeched", "%amount%");
            public final PlaceholderEndPoint<String> disabled = new PlaceholderEndPoint<>(this, "disabled", "%enchant%");
            public final PlaceholderEndPoint<Integer> quakeOnCooldown = new PlaceholderEndPoint<>(this, "quake-on-cooldown", "%number%");
            public final PlaceholderEndPoint<Integer> tooManyTotal = new PlaceholderEndPoint<>(this, "too_many_total", "%number%");
            public final PlaceholderEndPoint<Integer> tooManyCustom = new PlaceholderEndPoint<>(this, "too_many_custom", "%number%");

        }

        public final EnchantInfoSection enchantInfo = new EnchantInfoSection();
        public class EnchantInfoSection extends Section {
            private EnchantInfoSection() {
                super("enchant-info");
            }

            public final DefaultFormat defaultFormat = new DefaultFormat();
            public class DefaultFormat extends EndPoint {
                private DefaultFormat() {
                    super(EnchantInfoSection.this, "default-format");
                }

                public void sendTo(CommandSender sender, EnchantPlus enchant) {
                    if (isString()) {
                        sendMessage(sender, placeholder(
                            "%n", enchant.getId(),
                            "%a", "1",
                            "%b", String.valueOf(plugin.getMainConfig().getBy(enchant).getMaxLevel()),
                            "%s", EnchantInfoSection.this.enchant.getInfo(enchant)
                        ));
                    }
                }

                public void sendWithoutPrefixTo(CommandSender sender, EnchantPlus enchant) {
                    if (isString()) {
                        sendMessage(sender, false, placeholder(
                            "%n", plugin.getMainConfig().getBy(enchant).getDisplayName(),
                            "%a", "1",
                            "%b", String.valueOf(plugin.getMainConfig().getBy(enchant).getMaxLevel()),
                            "%s", EnchantInfoSection.this.enchant.getInfo(enchant)
                        ));
                    }
                }
            }

            public final EnchantSection enchant = new EnchantSection();
            public class EnchantSection extends Section {
                private final Map<EnchantPlus, NoPlaceholderEndPoint> index = new HashMap<>();

                private EnchantSection() {
                    super(EnchantInfoSection.this, "enchant");

                    for (EnchantPlus enchant : EnchantPlus.values()) {
                        index.put(enchant, new NoPlaceholderEndPoint(this, enchant.getId()));
                    }
                }

                public String getInfo(EnchantPlus enchant) {
                    NoPlaceholderEndPoint info = index.get(enchant);
                    if (info != null) {
                        return info.getColorCodesTranslatedValue();
                    } else {
                        return "";
                    }
                }
            }
        }

        public abstract class Section {
            private final Section parentSection;
            private final String sectionName;

            private Section(Section parentSection, String sectionName) {
                this.parentSection = parentSection;
                this.sectionName = sectionName;
            }

            private Section(String sectionName) {
                this(null, sectionName);
            }

            public String getPath() {
                if (parentSection == null) {
                    return sectionName;
                } else {
                    return parentSection.getPath() + get().options().pathSeparator() + sectionName;
                }
            }

            public boolean isString() {
                return get().isString(getPath());
            }

            public String getValue() {
                return get().getString(getPath());
            }

            public String getColorCodesTranslatedValue() {
                return ChatColor.translateAlternateColorCodes('&', getValue());
            }
        }

        // Implement send method with placeholder for each end point.
        public abstract class EndPoint extends Section {

            private EndPoint(Section parentSection, String sectionName) {
                super(parentSection, sectionName);
            }

            private EndPoint(String sectionName) {
                super(null, sectionName);
            }

            protected void sendMessage(CommandSender sender, boolean addPrefix, Map<String, String> placeholders) {
                String prefix = addPrefix ? "&b&lEnchants&9&l+ &3&l> &r" : "";
                String message = prefix + getValue();
                for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                    message = message.replace(placeholder.getKey(), placeholder.getValue());
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }

            protected void sendMessage(CommandSender sender, Map<String, String> placeholders) {
                sendMessage(sender, true, placeholders);
            }

            protected void sendMessage(CommandSender sender) {
                sendMessage(sender, placeholder());
            }

            protected void sendMessage(CommandSender sender, boolean addPrefix) {
                sendMessage(sender, addPrefix, placeholder());
            }

            protected Map<String, String> placeholder(String... elements) {
                if (elements.length % 2 != 0) {
                    throw new IllegalArgumentException("elements length cannot be odd number.");
                }

                Map<String, String> result = new HashMap<>();
                for (int i = 0; i < elements.length; i += 2) {
                    if (elements.length - 1 < i + 1) {
                        break;
                    }

                    String key = elements[i];
                    if (key == null || elements[i + 1] == null) {
                        continue;
                    }
                    result.put(key, elements[i + 1]);
                }

                return result;
            }
        }

        // If the endpoint do not have placeholder, it can be abstractized.
        public class NoPlaceholderEndPoint extends EndPoint {

            private NoPlaceholderEndPoint(Section parentSection, String sectionName) {
                super(parentSection, sectionName);
            }

            private NoPlaceholderEndPoint(String sectionName) {
                super(null, sectionName);
            }

            public void sendTo(CommandSender sender) {
                if (isString()) {
                    sendMessage(sender);
                }
            }

            public void sendWithoutPrefixTo(CommandSender sender) {
                if (isString()) {
                    sendMessage(sender, false);
                }
            }
        }

        public class PlaceholderEndPoint<T> extends EndPoint {

            private final String placeholderKey;

            private PlaceholderEndPoint(Section parentSection, String sectionName, String placeholderKey) {
                super(parentSection, sectionName);
                this.placeholderKey = placeholderKey;
            }

            private PlaceholderEndPoint(String sectionName, String placeholderKey) {
                super(sectionName);
                this.placeholderKey = placeholderKey;
            }

            public void sendTo(CommandSender sender, T placeholder) {
                if (isString()) {
                    sendMessage(sender, placeholder(placeholderKey, placeholder.toString()));
                }
            }

            public void sendWithoutPrefixTo(CommandSender sender, T placeholder) {
                if (isString()) {
                    sendMessage(sender, false, placeholder(placeholderKey, placeholder.toString()));
                }
            }
        }

        public class BiPlaceholderEndPoint<T1, T2> extends EndPoint {

            private final String placeholderKey1;
            private final String placeholderKey2;

            private BiPlaceholderEndPoint(Section parentSection, String sectionName, String placeholderKey1, String placeholderKey2) {
                super(parentSection, sectionName);
                this.placeholderKey1 = placeholderKey1;
                this.placeholderKey2 = placeholderKey2;
            }

            private BiPlaceholderEndPoint(String sectionName, String placeholderKey1, String placeholderKey2) {
                super(sectionName);
                this.placeholderKey1 = placeholderKey1;
                this.placeholderKey2 = placeholderKey2;
            }

            public void sendTo(CommandSender sender, T1 placeholder1, T2 placeholder2) {
                if (isString()) {
                    sendMessage(sender, placeholder(placeholderKey1, placeholder1.toString(), placeholderKey2, placeholder2.toString()));
                }
            }

            public void sendWithoutPrefixTo(CommandSender sender, T1 placeholder1, T2 placeholder2) {
                if (isString()) {
                    sendMessage(sender, false, placeholder(placeholderKey1, placeholder1.toString(), placeholderKey2, placeholder2.toString()));
                }
            }
        }
    }

    /**
     * Gets {@link Language} from map or plugin data folder. If language is not
     * included jar file and plugin data folder, method will try to use default
     * language "en_us". If default "en_us" language is not available, throw
     * {@link IllegalStateException}.
     *
     * @param viewer viewer to retrieve its language code.
     * @return {@link Language}.
     * @throws IllegalStateException If retrieved language and default language
     *                               are not available. (Not included defualt
     *                               language file in jar, report to developer.)
     */
    public Language getLanguage(CommandSender viewer) {
        if (viewer instanceof Player) {
            return getLanguage(getLocale(viewer));
        } else {
            return getDefaultLanguage();
        }

    }

    private Language getLanguage(String code) throws IllegalStateException {
        Language lang = languages.get(code);
        if (lang != null) {
            return lang;
        }

        try {
            lang = new Language(code);
            languages.put(code, lang);
            return lang;
        } catch (IllegalArgumentException e) {
            if (!code.equals("en_us")) {
                return getDefaultLanguage();
            } else {
                throw new IllegalStateException("default language en_us is not included in jar.");
            }
        }
    }

    public Language getDefaultLanguage() {
        return getLanguage(defaultLanguage.get());
    }

    /**
     * Reload all the language config on map.
     */
    public void reload() {
        languages.values().forEach(Language::reload);
    }


    private final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    String getLocale(CommandSender sender) {
        if (sender instanceof Player) {
            if (version.compareTo("v1_12") >= 0 && !version.startsWith("v1_11")) {
                return ((Player) sender).getLocale();
            } else {
                return defaultLanguage.get();
            }
        } else {
            return defaultLanguage.get();
        }
    }

}