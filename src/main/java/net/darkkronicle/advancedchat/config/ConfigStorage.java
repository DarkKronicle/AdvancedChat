package net.darkkronicle.advancedchat.config;

import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;

import java.util.ArrayList;

// Used to store values into config.json
public class ConfigStorage {

    public ArrayList<Filter> filters = new ArrayList<>();
    public ArrayList<ChatTab> tabs = new ArrayList<>();

    public String timeFormat = "hh:mm";
    public String replaceFormat = "[%TIME%] ";
    public ColorUtil.SimpleColor timeColor = ColorUtil.WHITE;

    public ChatConfig chatConfig = new ChatConfig();
    public static class ChatConfig {
        public int height = 171;
        public int width =  280;
        public int lineSpace = 9;
        public int yOffset = 30;
        public int storedLines = 200;

        public ColorUtil.SimpleColor hudBackground = ColorUtil.BLACK.withAlpha(100);
        public ColorUtil.SimpleColor emptyText = ColorUtil.WHITE;
        public boolean showTime = false;

    }

    public ChatLogConfig chatLogConfig = new ChatLogConfig();

    public static class ChatLogConfig {
        public int storedLines = 1000;
        public boolean showTime = false;

    }

}
