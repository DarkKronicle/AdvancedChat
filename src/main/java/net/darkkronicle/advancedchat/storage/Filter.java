package net.darkkronicle.advancedchat.storage;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;
import lombok.Data;
import net.darkkronicle.advancedchat.config.options.ConfigSimpleColor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Filter Storage
 * This class is used to store data for filters. Each filter is based off of this class. These are stored in an ArrayList for later usage.
 */

@Environment(EnvType.CLIENT)
@Data
public class Filter {

    private static String translate(String key) {
        return StringUtils.translate("advancedchat.config.filter." + key);
    }

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
            new ConfigOptionList(translate("replacetype"), ReplaceType.ONLYMATCH, translate("info.replacetype")));

    public ReplaceType getReplace() {
        return ReplaceType.fromReplaceTypeString(replaceType.config.getStringValue());
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
            new ConfigOptionList(translate("notifysound"), NotifySound.ARROW_HIT_PLAYER, translate("info.notifysound")));

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
            new ConfigSimpleColor(translate("background"), ColorUtil.WHITE, translate("info.background")));

    private ArrayList<Filter> children = new ArrayList<>();

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
            hover.add(s.replaceAll(Pattern.quote("<name>"), name.config.getStringValue())
                    .replaceAll(Pattern.quote("<active>"), active.config.getStringValue())
                    .replaceAll(Pattern.quote("<find>"), findString.config.getStringValue())
                    .replaceAll(Pattern.quote("<findtype>"), getFind().getDisplayName()));
        }
        return hover;
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

    public enum ReplaceType implements IConfigOptionListEntry {
        NONE("none"),
        ONLYMATCH("onlymatch"),
        FULLMESSAGE("fullmessage"),
        CHILDREN("children")
        ;
        public final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.replacetype." + key);
        }

        ReplaceType(String configString) {
            this.configString = configString;
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
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromReplaceTypeString(value);
        }

        public static ReplaceType fromReplaceTypeString(String replacetype) {
            for (ReplaceType r : ReplaceType.values()) {
                if (r.configString.equals(replacetype)) {
                    return r;
                }
            }
            return ReplaceType.NONE;
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
            return StringUtils.translate("advancedchat.config.replacetype." + key);
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
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromNotifySoundString(value);
        }

        public static NotifySound fromNotifySoundString(String notifysound) {
            for (NotifySound r : NotifySound.values()) {
                if (r.configString.equals(notifysound)) {
                    return r;
                }
            }
            return NotifySound.ARROW_HIT_PLAYER;
        }
    }

}
