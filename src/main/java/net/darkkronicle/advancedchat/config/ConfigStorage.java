/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package net.darkkronicle.advancedchat.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.options.ConfigSimpleColor;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Used to store values into config.json
public class ConfigStorage implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = AdvancedChat.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static final ArrayList<Filter> FILTERS = new ArrayList<>();
    public static final ArrayList<ChatTab> TABS = new ArrayList<>();


    public static class SaveableConfig<T extends IConfigBase> {
        public final T config;
        public final String key;

        private SaveableConfig(String key, T config) {
            this.key = key;
            this.config = config;
        }

        public static <C extends IConfigBase> SaveableConfig<C> fromConfig(String key, C config) {
            return new SaveableConfig<>(key, config);
        }

    }

    public static class Chat {

        public static final String NAME = "chat";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.chat." + key);
        }

        public final static SaveableConfig<ConfigString> TIME_FORMAT = SaveableConfig.fromConfig("timeFormat",
                new ConfigString(translate("timeformat"), "hh:mm", translate("info.timeformat")));

        public final static SaveableConfig<ConfigString> TIME_TEXT_FORMAT = SaveableConfig.fromConfig("timeTextFormat",
                new ConfigString(translate("timetextformat"), "[%TIME%] ", translate("config.info.timetextformat")));

        public final static SaveableConfig<ConfigSimpleColor> TIME_COLOR = SaveableConfig.fromConfig("time_color",
                new ConfigSimpleColor(translate("timecolor"), ColorUtil.WHITE, translate("info.timecolor")));

        public final static SaveableConfig<ConfigBoolean> CLEAR_ON_DISCONNECT = SaveableConfig.fromConfig("clearOnDisconnect",
                new ConfigBoolean(translate("clearondisconnect"), true, translate("info.clearondisconnect")));

        public final static SaveableConfig<ConfigInteger> CHAT_STACK = SaveableConfig.fromConfig("chatStack",
                new ConfigInteger(translate("chatstack"), 0, 0, 20, translate("info.chatstack")));

        public final static SaveableConfig<ConfigBoolean> CHAT_HEADS = SaveableConfig.fromConfig("chatHeads",
                new ConfigBoolean(translate("chatheads"), false, translate("info.chatheads")));

        public final static SaveableConfig<ConfigString> CHAT_HEAD_REGEX = SaveableConfig.fromConfig("chatHeadRegex",
                new ConfigString(translate("chatheadregex"), "[A-Za-z0-9_§]{3,16}", translate("info.chatheadregex")));

        public final static SaveableConfig<ConfigBoolean> SHOW_TIME = SaveableConfig.fromConfig("showTime",
                new ConfigBoolean(translate("showtime"), false, translate("info.showtime")));

        public final static SaveableConfig<ConfigInteger> STORED_LINES = SaveableConfig.fromConfig("storedLines",
                new ConfigInteger(translate("storedlines"), 200, 20, 1000, translate("info.storedlines")));

        public final static ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS = ImmutableList.of(
                TIME_FORMAT,
                TIME_TEXT_FORMAT,
                TIME_COLOR,
                SHOW_TIME,
                CLEAR_ON_DISCONNECT,
                CHAT_STACK,
                CHAT_HEADS,
                CHAT_HEAD_REGEX,
                STORED_LINES
        );

    }

    public static class ChatScreen {

        public static final String NAME = "chatscreen";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.chatscreen." + key);
        }

        public final static SaveableConfig<ConfigInteger> HEIGHT = SaveableConfig.fromConfig("height",
                new ConfigInteger(translate("height"), 117, 20, 400, translate("info.height")));
        public final static SaveableConfig<ConfigInteger> WIDTH = SaveableConfig.fromConfig("width",
                new ConfigInteger(translate("width"), 280, 100, 600, translate("info.width")));
        public final static SaveableConfig<ConfigBoolean> SHOW_TABS = SaveableConfig.fromConfig("showTabs",
                new ConfigBoolean(translate("showtabs"), true, translate("info.showtabs")));
        public final static SaveableConfig<ConfigOptionList> VISIBILITY = SaveableConfig.fromConfig("visibility",
                new ConfigOptionList(translate("visilibity"), Visibility.VANILLA, translate("info.visibility")));
        public final static SaveableConfig<ConfigInteger> LINE_SPACE = SaveableConfig.fromConfig("lineSpace",
                new ConfigInteger(translate("linespace"), 9, 8, 20, translate("info.linespace")));
        public final static SaveableConfig<ConfigInteger> X = SaveableConfig.fromConfig("x",
                new ConfigInteger(translate("x"), 0, 0, 4000, translate("info.x")));
        public final static SaveableConfig<ConfigInteger> Y = SaveableConfig.fromConfig("y",
                new ConfigInteger(translate("y"), 30, 0, 4000, translate("info.y")));
        public final static SaveableConfig<ConfigDouble> CHAT_SCALE = SaveableConfig.fromConfig(translate("chatScale"),
                new ConfigDouble(translate("chatscale"), 1, 0, 1, translate("info.chatscale")));
        public final static SaveableConfig<ConfigInteger> FADE_TIME = SaveableConfig.fromConfig("fadeTime",
                new ConfigInteger(translate("fadetime"), 40, 0, 200, translate("info.fadetime")));
        public final static SaveableConfig<ConfigInteger> FADE_START = SaveableConfig.fromConfig("fadeStart",
                new ConfigInteger(translate("fadestart"), 100, 20, 1000, translate("info.fadestart")));
        public final static SaveableConfig<ConfigOptionList> FADE_TYPE = SaveableConfig.fromConfig("fadeType",
                new ConfigOptionList(translate("fadetype"), Easing.LINEAR, translate("info.fadetype")));
        public final static SaveableConfig<ConfigSimpleColor> HUD_BACKGROUND_COLOR = SaveableConfig.fromConfig("hudbackgroundcolor",
                new ConfigSimpleColor(translate("hudbackgroundcolor"), ColorUtil.BLACK.withAlpha(100), translate("info.hudbackgroundcolor")));
        public final static SaveableConfig<ConfigSimpleColor> EMPTY_TEXT_COLOR = SaveableConfig.fromConfig("emptyTextColor",
                new ConfigSimpleColor(translate("emptytextcolor"), ColorUtil.WHITE, translate("info.emptytextcolor")));
        public final static SaveableConfig<ConfigInteger> TAB_SIDE_CHARS = SaveableConfig.fromConfig("tabSideChars",
                new ConfigInteger(translate("tabsidechars"), 3, 1, 10, translate("info.tabsidechars")));
        public final static SaveableConfig<ConfigOptionList> HUD_LINE_TYPE = SaveableConfig.fromConfig("hudLineType",
                new ConfigOptionList(translate("hudlinetype"), HudLineType.FULL, translate("info.hudlinetype")));
        public final static SaveableConfig<ConfigBoolean> ALTERNATE_LINES = SaveableConfig.fromConfig("alternateLines",
                new ConfigBoolean(translate("alternatelines"), false, translate("info.alternatelines")));

        public final static ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS = ImmutableList.of(
                HEIGHT,
                WIDTH,
                SHOW_TABS,
                VISIBILITY,
                LINE_SPACE,
                X,
                Y,
                CHAT_SCALE,
                FADE_TIME,
                FADE_START,
                FADE_TYPE,
                HUD_BACKGROUND_COLOR,
                EMPTY_TEXT_COLOR,
                TAB_SIDE_CHARS,
                HUD_LINE_TYPE,
                ALTERNATE_LINES
        );
    }


    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                readOptions(root, ConfigStorage.Chat.NAME, Chat.OPTIONS);
                readOptions(root, ConfigStorage.ChatScreen.NAME, ConfigStorage.ChatScreen.OPTIONS);

                int version = JsonUtils.getIntegerOrDefault(root, "config_version", 0);

           }
        }
    }

    public static void saveFromFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            writeOptions(root, ConfigStorage.Chat.NAME, ConfigStorage.Chat.OPTIONS);
            writeOptions(root, ConfigStorage.ChatScreen.NAME, ConfigStorage.ChatScreen.OPTIONS);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    public static void readOptions(JsonObject root, String category, List<SaveableConfig<?>> options) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null) {
            for (SaveableConfig<?> conf : options) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }
        }
    }

    public static void writeOptions(JsonObject root, String category, List<SaveableConfig<?>> options) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (SaveableConfig<?> option : options) {
            obj.add(option.key, option.config.getAsJsonElement());
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveFromFile();
    }

    public static class ChatLogConfig {
        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.chatlog." + key);
        }

        public final static SaveableConfig<ConfigInteger> STORED_LINES = SaveableConfig.fromConfig("storedlines",
                new ConfigInteger(translate("storedlines"), 1000, 20, 5000, translate("info.storedlines")));
        public final static SaveableConfig<ConfigBoolean> SHOW_TIME = SaveableConfig.fromConfig("showtime",
                new ConfigBoolean(translate("showtime"), false, translate("info.showtime")));

        public final static ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS = ImmutableList.of(
                STORED_LINES,
                SHOW_TIME
        );

    }

    public enum HudLineType implements IConfigOptionListEntry {
        FULL("full"),
        COMPACT("compact")
        ;

        public final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.hudlinetype." + key);
        }

        HudLineType(String configString) {
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
            return fromHudLineTypeString(value);
        }

        public static HudLineType fromHudLineTypeString(String hudlinetype) {
            for (HudLineType h : HudLineType.values()) {
                if (h.configString.equals(hudlinetype)) {
                    return h;
                }
            }
            return HudLineType.FULL;
        }
    }

    public enum Easing implements IConfigOptionListEntry, EasingMethod {
        LINEAR("linear", EasingMethod.EasingMethodImpl.LINEAR),
        SINE("sine", EasingMethod.EasingMethodImpl.SINE),
        QUAD("quad", EasingMethod.EasingMethodImpl.QUAD),
        QUART("quart", EasingMethod.EasingMethodImpl.QUART),
        CIRC("circ", EasingMethod.EasingMethodImpl.CIRC),
        ;

        public final EasingMethod ease;
        public final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.easing." + key);
        }

        Easing(String configString, EasingMethod ease) {
            this.ease = ease;
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
            return fromEasingString(value);
        }

        public static Easing fromEasingString(String visibility) {
            for (Easing e : Easing.values()) {
                if (e.configString.equals(visibility)) {
                    return e;
                }
            }
            return Easing.LINEAR;
        }


        @Override
        public double apply(double v) {
            return ease.apply(v);
        }
    }

    public enum Visibility implements IConfigOptionListEntry {
        VANILLA("vanilla"),
        ALWAYS("always"),
        FOCUSONLY("focus_only");

        private final String configString;

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.visibility." + key);
        }

        Visibility(String configString) {
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
            return fromVisibilityString(value);
        }

        public static Visibility fromVisibilityString(String visibility) {
            for (Visibility v : Visibility.values()) {
                if (v.configString.equals(visibility)) {
                    return v;
                }
            }
            return Visibility.VANILLA;
        }
    }

}
