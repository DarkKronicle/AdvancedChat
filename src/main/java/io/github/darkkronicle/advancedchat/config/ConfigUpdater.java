package io.github.darkkronicle.advancedchat.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ConfigUpdater {

    public static void update() {
        File dir = FileUtils.getConfigDirectory();
        if ((!dir.exists() || !dir.isDirectory()) && !dir.mkdirs()) {
            return;
        }

        File oldF = new File(dir, "advancedchat/filterconfig.json");
        if (!oldF.exists()) {
            return;
        }

        JsonElement element = JsonUtils.parseJsonFile(oldF);
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject obj = element.getAsJsonObject();
        setConfig(obj);

    }

    private static void setConfig(JsonObject obj) {
        ConfigStorage.General.CLEAR_ON_DISCONNECT.config.setBooleanValue(obj.get("clearOnDisconnect").getAsBoolean());
        ConfigStorage.ChatScreen.VISIBILITY.config.setValueFromString(obj.get("visibility").getAsString().toLowerCase());
        ConfigStorage.General.CHAT_STACK.config.setIntegerValue(obj.get("chatStack").getAsInt());
        ConfigStorage.ChatScreen.ALTERNATE_LINES.config.setBooleanValue(obj.get("alternatelines").getAsBoolean());
        ConfigStorage.General.TIME_FORMAT.config.setValueFromString(obj.get("timeFormat").getAsString());
        ConfigStorage.General.TIME_TEXT_FORMAT.config.setValueFromString(obj.get("replaceFormat").getAsString());
        ConfigStorage.General.TIME_COLOR.config.setIntegerValue(getSimpleColor(obj.get("timeColor")).color());
        setChatConfig(obj.get("chatConfig").getAsJsonObject());
        setLogConfig(obj.get("chatLogConfig").getAsJsonObject());
        JsonArray filt = obj.get("filters").getAsJsonArray();
        ConfigStorage.FILTERS.clear();
        for (JsonElement e : filt) {
            if (e.isJsonObject()) {
                ConfigStorage.FILTERS.add(getFilter(e.getAsJsonObject()));
            }
        }
        JsonArray tabs = obj.get("tabs").getAsJsonArray();
        ConfigStorage.TABS.clear();
        for (JsonElement e : tabs) {
            if (e.isJsonObject()) {
                ConfigStorage.TABS.add(getTab(e.getAsJsonObject()));
            }
        }
    }

    private static void setChatConfig(JsonObject obj) {
        ConfigStorage.ChatScreen.HEIGHT.config.setIntegerValue(obj.get("height").getAsInt());
        ConfigStorage.ChatScreen.WIDTH.config.setIntegerValue(obj.get("width").getAsInt());
        ConfigStorage.ChatScreen.X.config.setIntegerValue(obj.get("xOffset").getAsInt());
        ConfigStorage.ChatScreen.Y.config.setIntegerValue(obj.get("yOffset").getAsInt());
        ConfigStorage.ChatScreen.STORED_LINES.config.setIntegerValue(obj.get("storedLines").getAsInt());
        ConfigStorage.ChatScreen.CHAT_SCALE.config.setDoubleValue(obj.get("chatscale").getAsDouble());
        ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.setIntegerValue(getSimpleColor(obj.get("hudBackground")).color());
        ConfigStorage.ChatScreen.EMPTY_TEXT_COLOR.config.setIntegerValue(getSimpleColor(obj.get("emptyText")).color());
        ConfigStorage.ChatScreen.TAB_SIDE_CHARS.config.setIntegerValue(getSimpleColor(obj.get("sideChars")).color());
    }

    private static void setLogConfig(JsonObject obj) {
        ConfigStorage.ChatLog.SHOW_TIME.config.setBooleanValue(obj.get("showTime").getAsBoolean());
        ConfigStorage.ChatLog.STORED_LINES.config.setIntegerValue(obj.get("storedLines").getAsInt());
    }

    private static Filter getFilter(JsonObject obj) {
        Filter f = new Filter();
        f.getName().config.setValueFromString(obj.get("name").getAsString());
        f.getActive().config.setBooleanValue(obj.get("active").getAsBoolean());
        f.getFindString().config.setValueFromString(obj.get("findString").getAsString());
        f.getFindType().config.setValueFromString(obj.get("findType").getAsString().toLowerCase());
        f.getReplaceType().config.setValueFromString(obj.get("replaceType").getAsString().toLowerCase().replaceAll("_", ""));
        f.getReplaceTo().config.setValueFromString(obj.get("replaceTo").getAsString());
        f.getBackgroundColor().config.setIntegerValue(getSimpleColor(obj.get("color")).color());
        f.getTextColor().config.setIntegerValue(getSimpleColor(obj.get("color")).color());
        f.getReplaceBackgroundColor().config.setBooleanValue(obj.get("replaceBackgroundColor").getAsBoolean());
        f.getReplaceTextColor().config.setBooleanValue(obj.get("replaceTextColor").getAsBoolean());
        return f;
    }

    private static ChatTab getTab(JsonObject obj) {
        ChatTab t = new ChatTab();
        t.getName().config.setValueFromString(obj.get("name").getAsString());
        t.getFindString().config.setValueFromString(obj.get("findString").getAsString());
        t.getFindType().config.setValueFromString(obj.get("findType").getAsString().toLowerCase());
        t.getAbbreviation().config.setValueFromString(obj.get("abreviation").getAsString());
        t.getForward().config.setBooleanValue(obj.get("forward").getAsBoolean());
        return t;
    }

    private static ColorUtil.SimpleColor getSimpleColor(JsonElement element) {
        if (!element.isJsonObject()) {
            return ColorUtil.WHITE;
        }
        JsonObject obj = element.getAsJsonObject();
        int color = obj.get("color").getAsInt();
        return new ColorUtil.SimpleColor(color);
    }

    public static boolean checkForOutdated() {
        File dir = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").toFile();
        File newF = new File(dir, ConfigStorage.CONFIG_FILE_NAME);
        if (newF.exists()) {
            return false;
        }
        File oldF = new File(dir, "advancedchat/filterconfig.json");
        return oldF.exists();
    }


}
