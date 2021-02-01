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

package net.darkkronicle.advancedchat.storage;

import lombok.Data;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.ChatLogLine;
import net.darkkronicle.advancedchat.gui.ChatLogScreen;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
@Data
public class ChatLogData {
    private ArrayList<ChatLogLine> rawMessages = new ArrayList<>();
    private ArrayList<ChatLogLine> formattedMessages = new ArrayList<>();
    private int lastWidth = 0;
    private int lastHeight = 0;
    private boolean chatLogTime = AdvancedChat.configStorage.chatLogConfig.showTime;
    private boolean chatHudTime = AdvancedChat.configStorage.chatConfig.showTime;

    public void addMessage(Text text, AbstractChatTab... tab) {
        addMessage(text, 0, LocalTime.now(), tab);
    }

    public void addMessage(Text text, int id, LocalTime time, AbstractChatTab... tab) {
        Text original = text;
        MinecraftClient client = MinecraftClient.getInstance();
        if (lastWidth == 0 && lastHeight == 0) {
            lastWidth = client.getWindow().getScaledWidth();
            lastHeight = client.getWindow().getScaledHeight();
        }

        boolean showtime = AdvancedChat.configStorage.chatLogConfig.showTime;
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(AdvancedChat.configStorage.timeFormat);
            SplitText split = new SplitText(text);
            split.addTime(format, time);
            text = split.getText();
        }

        ChatLogLine line = new ChatLogLine(original, id, tab, time);

        rawMessages.add(line);
        this.formattedMessages.add(0, new ChatLogLine(text, id, tab, time, line.getUuid()));

        int visibleMessagesMaxSize = AdvancedChat.configStorage.chatLogConfig.storedLines;
        while(this.rawMessages.size() > visibleMessagesMaxSize) {
            this.rawMessages.remove(this.rawMessages.size() - 1);
        }
        while(this.formattedMessages.size() > visibleMessagesMaxSize) {
            this.formattedMessages.remove(this.formattedMessages.size() - 1);
        }

    }

    public void checkLast() {
        Window window = MinecraftClient.getInstance().getWindow();
        if (lastHeight != window.getScaledHeight() || lastWidth != window.getScaledWidth()) {
            setLast();
            reformatMessages();
        }
    }

    public void setLast() {
        Window window = MinecraftClient.getInstance().getWindow();
        lastWidth = window.getScaledWidth();
        lastHeight = window.getScaledHeight();
    }

    public void reformatMessages() {
        this.formattedMessages.clear();
        for(int i = this.rawMessages.size() - 1; i >= 0; --i) {
            ChatLogLine chatHudLine = this.rawMessages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getTime(), chatHudLine.getTab());
        }

    }

    public boolean isChatHudTime() {
        if (chatHudTime != AdvancedChat.configStorage.chatConfig.showTime) {
            chatHudTime = AdvancedChat.configStorage.chatConfig.showTime;
            return true;
        }
        return false;
    }

    public boolean isChatLogTime() {
        if (chatLogTime != AdvancedChat.configStorage.chatLogConfig.showTime) {
            chatLogTime = AdvancedChat.configStorage.chatLogConfig.showTime;
            return true;
        }
        return false;
    }
}
