package net.darkkronicle.advancedchat.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
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
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.config.options.ConfigSimpleColor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.EasingMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Used to store values into config.json
@Environment(EnvType.CLIENT)
public class ConfigStorage implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = AdvancedChat.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static final ArrayList<Filter> FILTERS = new ArrayList<>();
    private static final String FILTER_KEY = "filters";
    public static final ArrayList<ChatTab> TABS = new ArrayList<>();
    private static final String TABS_KEY = "chattabs";


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

    public static class General {

        public static final String NAME = "general";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.general." + key);
        }

        public final static SaveableConfig<ConfigString> TIME_FORMAT = SaveableConfig.fromConfig("timeFormat",
                new ConfigString(translate("timeformat"), "hh:mm", translate("info.timeformat")));

        public final static SaveableConfig<ConfigString> TIME_TEXT_FORMAT = SaveableConfig.fromConfig("timeTextFormat",
                new ConfigString(translate("timetextformat"), "[%TIME%] ", translate("info.timetextformat")));

        public final static SaveableConfig<ConfigSimpleColor> TIME_COLOR = SaveableConfig.fromConfig("time_color",
                new ConfigSimpleColor(translate("timecolor"), ColorUtil.WHITE, translate("info.timecolor")));

        public final static SaveableConfig<ConfigBoolean> CLEAR_ON_DISCONNECT = SaveableConfig.fromConfig("clearOnDisconnect",
                new ConfigBoolean(translate("clearondisconnect"), true, translate("info.clearondisconnect")));

        public final static SaveableConfig<ConfigInteger> CHAT_STACK = SaveableConfig.fromConfig("chatStack",
                new ConfigInteger(translate("chatstack"), 0, 0, 20, translate("info.chatstack")));

        public final static SaveableConfig<ConfigBoolean> CHAT_HEADS = SaveableConfig.fromConfig("chatHeads",
                new ConfigBoolean(translate("chatheads"), false, translate("info.chatheads")));

        public final static SaveableConfig<ConfigString> MESSAGE_OWNER_REGEX = SaveableConfig.fromConfig("messageOwnerRegex",
                new ConfigString(translate("messageownerregex"), "[A-Za-z0-9_§]{3,16}", translate("info.messageownerregex")));


        public final static ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS = ImmutableList.of(
                TIME_FORMAT,
                TIME_TEXT_FORMAT,
                TIME_COLOR,
                CLEAR_ON_DISCONNECT,
                CHAT_STACK,
                CHAT_HEADS,
                MESSAGE_OWNER_REGEX
        );

    }

    public static class ChatScreen {

        public static final String NAME = "chatscreen";

        public static String translate(String key) {
            return StringUtils.translate("advancedchat.config.chatscreen." + key);
        }

        public final static SaveableConfig<ConfigInteger> WIDTH = SaveableConfig.fromConfig("width",
                new ConfigInteger(translate("width"), 280, 100, 600, translate("info.width")));
        public final static SaveableConfig<ConfigInteger> HEIGHT = SaveableConfig.fromConfig("height",
                new ConfigInteger(translate("height"), 117, 20, 400, translate("info.height")));
        public final static SaveableConfig<ConfigInteger> X = SaveableConfig.fromConfig("x",
                new ConfigInteger(translate("x"), 0, 0, 4000, translate("info.x")));
        public final static SaveableConfig<ConfigInteger> Y = SaveableConfig.fromConfig("y",
                new ConfigInteger(translate("y"), 30, 0, 4000, translate("info.y")));
        public final static SaveableConfig<ConfigInteger> MESSAGE_SPACE = SaveableConfig.fromConfig("messageSpace",
                new ConfigInteger(translate("messagespace"), 0, 0, 10, translate("info.messagespace")));
        public final static SaveableConfig<ConfigInteger> LINE_SPACE = SaveableConfig.fromConfig("lineSpace",
                new ConfigInteger(translate("linespace"), 9, 8, 20, translate("info.linespace")));
        public final static SaveableConfig<ConfigInteger> LEFT_PAD = SaveableConfig.fromConfig("leftPad",
                new ConfigInteger(translate("leftpad"), 2, 0, 20, translate("info.leftpad")));
        public final static SaveableConfig<ConfigInteger> RIGHT_PAD = SaveableConfig.fromConfig("rightPad",
                new ConfigInteger(translate("rightpad"), 2, 0, 20, translate("info.rightpad")));
        public final static SaveableConfig<ConfigInteger> BOTTOM_PAD = SaveableConfig.fromConfig("bottomPad",
                new ConfigInteger(translate("bottompad"), 1, 0, 20, translate("info.bottompad")));
        public final static SaveableConfig<ConfigInteger> TOP_PAD = SaveableConfig.fromConfig("topPad",
                new ConfigInteger(translate("toppad"), 0, 0, 20, translate("info.toppad")));
        public final static SaveableConfig<ConfigBoolean> SHOW_TABS = SaveableConfig.fromConfig("showTabs",
                new ConfigBoolean(translate("showtabs"), true, translate("info.showtabs")));
        public final static SaveableConfig<ConfigOptionList> VISIBILITY = SaveableConfig.fromConfig("visibility",
                new ConfigOptionList(translate("visibility"), Visibility.VANILLA, translate("info.visibility")));
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
        public final static SaveableConfig<ConfigBoolean> SHOW_TIME = SaveableConfig.fromConfig("showTime",
                new ConfigBoolean(translate("showtime"), false, translate("info.showtime")));
        public final static SaveableConfig<ConfigInteger> STORED_LINES = SaveableConfig.fromConfig("storedLines",
                new ConfigInteger(translate("storedlines"), 200, 20, 1000, translate("info.storedlines")));
        public final static SaveableConfig<ConfigBoolean> PERSISTENT_TEXT = SaveableConfig.fromConfig("persistentText",
                new ConfigBoolean(translate("persistenttext"), false, translate("info.persistenttext")));

        public final static ImmutableList<SaveableConfig<? extends IConfigBase>> OPTIONS = ImmutableList.of(
                WIDTH,
                HEIGHT,
                X,
                Y,
                MESSAGE_SPACE,
                LINE_SPACE,
                LEFT_PAD,
                RIGHT_PAD,
                BOTTOM_PAD,
                TOP_PAD,
                SHOW_TABS,
                VISIBILITY,
                CHAT_SCALE,
                FADE_TIME,
                FADE_START,
                FADE_TYPE,
                HUD_BACKGROUND_COLOR,
                EMPTY_TEXT_COLOR,
                TAB_SIDE_CHARS,
                HUD_LINE_TYPE,
                ALTERNATE_LINES,
                SHOW_TIME,
                STORED_LINES,
                PERSISTENT_TEXT
        );
    }


    public static void loadFromFile() {

        if (ConfigUpdater.checkForOutdated()) {
            Logger LOGGER = LogManager.getLogger();
            try {
                LOGGER.info("Old AdvancedChat configuration found! Updating now...");
                ConfigUpdater.update();
                saveFromFile();
                LOGGER.info("AdvancedChat update successful!");
                AdvancedChat.chatTab.setUpTabs();
                ChatDispatcher.getInstance().loadFilters();
                return;
            } catch (Exception e) {
                LOGGER.warn("Something went wrong when updating the old configuration file!");
                e.printStackTrace();
            }
        }

        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();
            ChatTab.ChatTabJsonSave tabSave = new ChatTab.ChatTabJsonSave();
            JsonElement element = parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                readOptions(root, General.NAME, General.OPTIONS);
                readOptions(root, ConfigStorage.ChatScreen.NAME, ConfigStorage.ChatScreen.OPTIONS);

                JsonElement o = root.get(FILTER_KEY);
                ConfigStorage.FILTERS.clear();
                if (o != null && o.isJsonArray()) {
                    for (JsonElement el : o.getAsJsonArray()) {
                        if (el.isJsonObject()) {
                            ConfigStorage.FILTERS.add(filterSave.load(el.getAsJsonObject()));
                        }
                    }
                }
                JsonElement t = root.get(TABS_KEY);
                ConfigStorage.TABS.clear();
                if (t != null && t.isJsonArray()) {
                    for (JsonElement el : t.getAsJsonArray()) {
                        if (el.isJsonObject()) {
                            ConfigStorage.TABS.add(tabSave.load(el.getAsJsonObject()));
                        }
                    }
                }

                int version = JsonUtils.getIntegerOrDefault(root, "configVersion", 0);

           }
        }
        AdvancedChat.chatTab.setUpTabs();
        ChatDispatcher.getInstance().loadFilters();
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



    public static void saveFromFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();
            ChatTab.ChatTabJsonSave tabSave = new ChatTab.ChatTabJsonSave();
            JsonObject root = new JsonObject();

            writeOptions(root, General.NAME, General.OPTIONS);
            writeOptions(root, ConfigStorage.ChatScreen.NAME, ConfigStorage.ChatScreen.OPTIONS);

            JsonArray arr = new JsonArray();
            for (Filter f : ConfigStorage.FILTERS) {
                arr.add(filterSave.save(f));
            }
            root.add(FILTER_KEY, arr);

            JsonArray tabs = new JsonArray();
            for (ChatTab t : ConfigStorage.TABS) {
                tabs.add(tabSave.save(t));
            }
            root.add(TABS_KEY, tabs);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
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

    // WINDOWS BAD AND MINECRAFT LIKES UTF-16
    public static JsonElement parseJsonFile(File file)
    {
        if (file != null && file.exists() && file.isFile() && file.canRead())
        {
            String fileName = file.getAbsolutePath();

            try
            {
                JsonParser parser = new JsonParser();
                Charset[] sets = new Charset[]{StandardCharsets.UTF_8, Charset.defaultCharset()};
                // Start to enforce UTF 8. Old files may be UTF-16
                for (Charset s : sets) {
                    JsonElement element;
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), s);
                    try {
                        element = parser.parse(reader);
                    } catch (Exception e) {
                        reader.close();
                        continue;
                    }
                    reader.close();

                    return element;
                }
            }
            catch (Exception e)
            {
                MaLiLib.logger.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    // WINDOWS BAD AND MINECRAFT LIKES UTF-16
    public static boolean writeJsonToFile(JsonObject root, File file) {
        OutputStreamWriter writer = null;

        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(JsonUtils.GSON.toJson(root));
            writer.close();

            return true;
        }
        catch (IOException e)
        {
            MaLiLib.logger.warn("Failed to write JSON data to file '{}'", file.getAbsolutePath(), e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (Exception e)
            {
                MaLiLib.logger.warn("Failed to close JSON file", e);
            }
        }

        return false;
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
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
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
        LINEAR("linear", Method.LINEAR),
        SINE("sine", Method.SINE),
        QUAD("quad", Method.QUAD),
        QUART("quart", Method.QUART),
        CIRC("circ", Method.CIRC),
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
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
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
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
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
