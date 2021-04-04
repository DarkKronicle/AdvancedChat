package net.darkkronicle.advancedchat.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;
import lombok.Data;
import net.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import net.darkkronicle.advancedchat.chat.registry.MatchReplaceRegistry;
import net.darkkronicle.advancedchat.config.options.ConfigSimpleColor;
import net.darkkronicle.advancedchat.filters.processors.ChatTabProcessor;
import net.darkkronicle.advancedchat.interfaces.IJsonSave;
import net.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Filter Storage
 * This class is used to store data for filters. Each filter is based off of this class. These are stored in an ArrayList for later usage.
 *
 * Note: this class has a natural ordering that is inconsistent with equals for ordering.
 */

@Environment(EnvType.CLIENT)
@Data
public class Filter implements Comparable<Filter> {

    private static String translate(String key) {
        return StringUtils.translate("advancedchat.config.filter." + key);
    }

    private Integer order = 0;

    /**
     * Name is only cosmetic. Shows up when editing filters. Way to distinguish filters for the player.
      */
    private ConfigStorage.SaveableConfig<ConfigString> name = ConfigStorage.SaveableConfig.fromConfig("name",
            new ConfigString(translate("name"), "Default", translate("info.name")));

    /**
     * Whether or not it should be used to filter chat messages currently.
     */
    private ConfigStorage.SaveableConfig<ConfigBoolean> active = ConfigStorage.SaveableConfig.fromConfig("active",
            new ConfigBoolean(translate("active"), false, translate("info.active")));

    /**
     * The Expression to find a match. The way it is interpreted is defined by findType.
     */
    private ConfigStorage.SaveableConfig<ConfigString> findString = ConfigStorage.SaveableConfig.fromConfig("findString",
            new ConfigString(translate("findstring"), "Hello", translate("info.findstring")));

    /** How findString is used.
     * LITERAL checks just for a match character to character.
     * UPPERLOWER is like literal, but ignore case.
     * REGEX interprets findString as a regular expression.
     */
    private ConfigStorage.SaveableConfig<ConfigOptionList> findType = ConfigStorage.SaveableConfig.fromConfig("findType",
            new ConfigOptionList(translate("findtype"), FindType.LITERAL, translate("info.findtype")));

    public FindType getFind() {
        return FindType.fromFindType(findType.config.getStringValue());
    }

    /**
     * How the found string is modified.
     * ONLYMATCH replaces only what was matched.
     * FULLLINE replaces the full message.
     */
    private ConfigStorage.SaveableConfig<ConfigOptionList> replaceType = ConfigStorage.SaveableConfig.fromConfig("replaceType",
            new ConfigOptionList(translate("replacetype"), MatchReplaceRegistry.getInstance().getDefaultOption(), translate("info.replacetype")));

    public IMatchReplace getReplace() {
        return ((MatchReplaceRegistry.MatchReplaceOption) replaceType.config.getOptionListValue()).getOption();
    }

    /**
     * What the found string replaces to. (ex. If replaceType is FULLLINE this will replace the message with this)
     */
    private ConfigStorage.SaveableConfig<ConfigString> replaceTo = ConfigStorage.SaveableConfig.fromConfig("replaceTo",
            new ConfigString(translate("replaceto"), "Welcome", translate("info.replaceto")));

    /** How the filter notifies the client of a found string.
     * SOUND plays a sound when the filter is triggered.
     */
    private ConfigStorage.SaveableConfig<ConfigOptionList> notifySound = ConfigStorage.SaveableConfig.fromConfig("notifySound",
            new ConfigOptionList(translate("notifysound"), NotifySound.NONE, translate("info.notifysound")));

    public NotifySound getSound() {
        return NotifySound.fromNotifySoundString(notifySound.config.getStringValue());
    }

    private ConfigStorage.SaveableConfig<ConfigDouble> soundPitch = ConfigStorage.SaveableConfig.fromConfig("soundPitch",
            new ConfigDouble(translate("soundpitch"), 1, 0.5, 3, translate("info.soundpitch")));

    private ConfigStorage.SaveableConfig<ConfigDouble> soundVolume = ConfigStorage.SaveableConfig.fromConfig("soundVolume",
            new ConfigDouble(translate("soundvolume"), 1, 0.5, 3, translate("info.soundvolume")));

    private ConfigStorage.SaveableConfig<ConfigBoolean> replaceTextColor = ConfigStorage.SaveableConfig.fromConfig("replaceTextColor",
            new ConfigBoolean(translate("replacetextcolor"), false, translate("info.replacetextcolor")));

    private ConfigStorage.SaveableConfig<ConfigSimpleColor> textColor = ConfigStorage.SaveableConfig.fromConfig("textColor",
            new ConfigSimpleColor(translate("textcolor"), ColorUtil.WHITE, translate("info.textcolor")));

    private ConfigStorage.SaveableConfig<ConfigBoolean> replaceBackgroundColor = ConfigStorage.SaveableConfig.fromConfig("replaceBackgroundColor",
            new ConfigBoolean(translate("replacebackgroundcolor"), false, translate("info.replacebackgroundcolor")));

    private ConfigStorage.SaveableConfig<ConfigSimpleColor> backgroundColor = ConfigStorage.SaveableConfig.fromConfig("backgroundColor",
            new ConfigSimpleColor(translate("backgroundcolor"), ColorUtil.WHITE, translate("info.backgroundcolor")));

    private ArrayList<Filter> children = new ArrayList<>();

    private ArrayList<IMatchProcessor> processors = new ArrayList<>(Collections.singleton(MatchProcessorRegistry.getInstance().getDefaultOption().getOption()));

    private final ImmutableList<ConfigStorage.SaveableConfig<?>> options = ImmutableList.of(
            name,
            active,
            findString,
            findType,
            replaceType,
            replaceTo,
            notifySound,
            soundPitch,
            soundVolume,
            replaceTextColor,
            textColor,
            replaceBackgroundColor,
            backgroundColor
    );

    public List<String> getWidgetHoverLines() {
        String translated = StringUtils.translate("advancedchat.config.filterdescription");
        ArrayList<String> hover = new ArrayList<>();
        for (String s : translated.split("\n")) {
            hover.add(s.replaceAll(Pattern.quote("<name>"), Matcher.quoteReplacement(name.config.getStringValue()))
                    .replaceAll(Pattern.quote("<active>"), Matcher.quoteReplacement(active.config.getStringValue()))
                    .replaceAll(Pattern.quote("<find>"), Matcher.quoteReplacement(findString.config.getStringValue()))
                    .replaceAll(Pattern.quote("<findtype>"), Matcher.quoteReplacement(getFind().getDisplayName())));
        }
        return hover;
    }

    public static class FilterJsonSave implements IJsonSave<Filter> {

        @Override
        public Filter load(JsonObject obj) {
            Filter f = new Filter();
            if (obj.get("order") != null) {
                try {
                    f.setOrder(obj.get("order").getAsInt());
                } catch (Exception e) {
                    f.setOrder(0);
                }
            }
            for (ConfigStorage.SaveableConfig<?> conf : f.getOptions()) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }

            JsonElement processors = obj.get("processors");
            if (processors != null && processors.isJsonArray()) {
                ArrayList<IMatchProcessor> newProcessors = new ArrayList<>();
                for (JsonElement o : processors.getAsJsonArray()) {
                    String s = o.getAsString();
                    MatchProcessorRegistry.MatchProcessorOption option = MatchProcessorRegistry.getInstance().get(s);
                    if (option != null) {
                        newProcessors.add(option.getOption());
                    }
                }
                f.setProcessors(newProcessors);
            }

            JsonElement children = obj.get("children");
            if (children != null && children.isJsonArray()) {
                ArrayList<Filter> child = new ArrayList<>();
                for (JsonElement o : children.getAsJsonArray()) {
                    if (o.isJsonObject()) {
                        child.add(load(o.getAsJsonObject()));
                    }
                }
                f.setChildren(child);
            }
            return f;
        }

        @Override
        public JsonObject save(Filter filter) {
            JsonArray children = new JsonArray();
            JsonObject obj = new JsonObject();
            for (ConfigStorage.SaveableConfig<?> option : filter.getOptions()) {
                obj.add(option.key, option.config.getAsJsonElement());
            }

            JsonArray processors = new JsonArray();
            for (MatchProcessorRegistry.MatchProcessorOption o : MatchProcessorRegistry.getInstance().getAll()) {
                if (filter.getProcessors().contains(o.getOption())) {
                    processors.add(o.getStringValue());
                }
            }
            obj.add("processors", processors);

            for (Filter c : filter.getChildren()) {
                children.add(save(c));
            }
            obj.add("children", children);
            obj.addProperty("order", filter.getOrder());
            return obj;
        }
    }

    @Override
    public int compareTo(@NotNull Filter o) {
        return order.compareTo(o.order);
    }


    public enum FindType implements IConfigOptionListEntry {
        LITERAL("literal"),
        UPPERLOWER("upperlower"),
        REGEX("regex"),
        ALL("all")
        ;
        public final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.findtype." + key);
        }

        FindType(String configString) {
            this.configString = configString   ;
        }

        @Override
        public String getStringValue() {
            return configString;
        }

        @Override
        public String getDisplayName() {
            return translate(configString);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                id++;
            } else {
                id--;
            }
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromFindType(value);
        }

        public static FindType fromFindType(String findtype) {
            for (FindType r : FindType.values()) {
                if (r.configString.equals(findtype)) {
                    return r;
                }
            }
            return FindType.LITERAL;
        }
    }

    public enum NotifySound implements IConfigOptionListEntry {
        NONE("none", null),
        ARROW_HIT_PLAYER("arrow_hit_player", SoundEvents.ENTITY_ARROW_HIT_PLAYER),
        ANVIL_BREAK("anvil_break", SoundEvents.BLOCK_ANVIL_BREAK),
        BEACON_ACTIVATE("beacon_activate", SoundEvents.BLOCK_BEACON_ACTIVATE),
        ELDER_GUARDIAN_CURSE("elder_guardian_curse", SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE),
        ENDERMAN_TELEPORT("enderman_teleport", SoundEvents.ENTITY_ENDERMAN_TELEPORT),
        WOLOLO("wololo", SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO),
        BELL("bell_use", SoundEvents.BLOCK_BELL_USE),
        CLICK("button_click", SoundEvents.UI_BUTTON_CLICK),
        HUSK_TO_ZOMBIE("husk_to_zombie", SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE),
        GLASS_BREAK("glass_break", SoundEvents.BLOCK_GLASS_BREAK)
        ;
        public final String configString;
        public final SoundEvent event;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.notifysound." + key);
        }

        NotifySound(String configString, SoundEvent sound) {
            this.configString = configString;
            this.event = sound;
        }

        @Override
        public String getStringValue() {
            return configString;
        }

        @Override
        public String getDisplayName() {
            return translate(configString);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                id++;
            } else {
                id--;
            }
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromNotifySoundString(value);
        }

        public static NotifySound fromNotifySoundName(String notifysound) {
            for (NotifySound r : NotifySound.values()) {
                if (r.event == null) {
                    continue;
                }
                if (r.event.getId().getPath().replaceAll("\\.", "_").toLowerCase().equalsIgnoreCase(notifysound)) {
                    return r;
                }
            }
            return NotifySound.NONE;
        }

        public static NotifySound fromNotifySoundString(String notifysound) {
            for (NotifySound r : NotifySound.values()) {
                if (r.configString.equals(notifysound)) {
                    return r;
                }
            }
            return NotifySound.NONE;
        }
    }

}
