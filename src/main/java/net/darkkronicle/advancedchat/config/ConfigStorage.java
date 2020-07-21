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

    public Visibility visibility = Visibility.VANILLA;

    public int chatStack = 0;

    public boolean alternatelines = false;

    public ChatConfig chatConfig = new ChatConfig();
    public static class ChatConfig {
        public int height = 171;
        public int width =  280;
        public int lineSpace = 9;
        public int yOffset = 30;
        public int xOffset = 0;
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

    public enum Visibility {
        VANILLA,
        ALWAYS,
        FOCUSONLY
    }

}
