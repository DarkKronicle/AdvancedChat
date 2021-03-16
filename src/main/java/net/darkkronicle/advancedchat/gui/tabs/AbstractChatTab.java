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

package net.darkkronicle.advancedchat.gui.tabs;

import lombok.Data;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatMessage;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Base ChatTab that allows for custom chat tabs in AdvancedChatHud.
 */
@Environment(EnvType.CLIENT)
@Data
public abstract class AbstractChatTab {

    // Each tab stores their own messages.
    public final List<AdvancedChatMessage> messages = new ArrayList<>();
    private final String name;
    private final String abreviation;
    private final AdvancedChatHud hud;
    private final MinecraftClient client;

    public AbstractChatTab(String name, String abreviation) {
        hud = AdvancedChat.getAdvancedChatHud();
        client = MinecraftClient.getInstance();
        this.name = name;
        this.abreviation = abreviation;
    }

    public int getLineCount() {
        int i = 0;
        for (AdvancedChatMessage message : messages) {
            i += message.getLineCount();
        }
        return i;
    }

    /**
     * If the inputted message should be put into the chat tab.
     *
     * @param text Object to search.
     * @return True if it should be added.
     */
    public abstract boolean shouldAdd(Text text);

    /**
     * Method to reformat the messages if Chat size changes or something.
     */
    public void reset() {
        int width = MathHelper.floor(AdvancedChatHud.getScaledWidth() - 5);
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            this.messages.get(i).formatChildren(width);
        }
    }

    public AdvancedChatMessage.AdvancedChatLine getLine(int line) {
        int oldline = 0;
        for (AdvancedChatMessage m : messages) {
            int newline = oldline + m.getLineCount();
            if (oldline <= line && newline > line) {
                return m.getLines().get(line - oldline);
            }
            oldline = newline;
        }
        return null;
    }

    public AdvancedChatMessage getMessageFromLine(int line) {
        int oldline = 0;
        for (AdvancedChatMessage m : messages) {
            int newline = oldline + m.getLineCount();
            if (oldline <= line && newline > line) {
                return m;
            }
            oldline = newline;
        }
        return null;
    }

    /**
     * Used for adding messages into the tab.
     *
     * @param text      Text to add.
     * @param messageId ID of message.
     * @param timestamp Amount of ticks when it was created.
     */
    public void addMessage(Text text, int messageId, int timestamp) {
        addMessage(text, messageId, timestamp, LocalTime.now());
    }

    public void addMessage(Text text, int messageId, int timestamp, LocalTime time) {
        addMessage(text, messageId, timestamp, time, null);
    }

    public void addMessage(Text text, int messageId, int timestamp, LocalTime time, PlayerListEntry playerInfo) {
        if (!shouldAdd(text)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        for (int i = 0; i < ConfigStorage.Chat.CHAT_STACK.config.getIntegerValue() && i < messages.size(); i++) {
            AdvancedChatMessage chatLine = messages.get(i);
            if (text.getString().equals(chatLine.getRawText().getString())) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }

        boolean showtime = ConfigStorage.Chat.SHOW_TIME.config.getBooleanValue();
        Text original = text;
        if (showtime) {
            SplitText split = new SplitText(text);
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.Chat.TIME_FORMAT.config.getStringValue());
            split.addTime(format, time);
            text = split.getText();
        }

        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        int width = MathHelper.floor(AdvancedChatHud.getScaledWidth() - 5);

        AdvancedChatMessage line = AdvancedChatMessage.builder().text(text).originalText(original).owner(playerInfo).id(messageId).width(width).creationTick(timestamp).time(time).build();
        this.messages.add(0, line);

        hud.messageAddedToTab(this);

        int visibleMessagesMaxSize = ConfigStorage.Chat.STORED_LINES.config.getIntegerValue();
        while (this.messages.size() > visibleMessagesMaxSize) {
            this.messages.remove(this.messages.size() - 1);
        }
    }

    public void removeMessage(int messageId) {
        Iterator iterator = this.messages.iterator();

        AdvancedChatMessage chatHudLine2;
        while (iterator.hasNext()) {
            chatHudLine2 = (AdvancedChatMessage) iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
            }
        }
    }

}
