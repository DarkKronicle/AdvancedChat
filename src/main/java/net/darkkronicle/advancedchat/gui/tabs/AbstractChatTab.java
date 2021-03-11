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
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatLine;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base ChatTab that allows for custom chat tabs in AdvancedChatHud.
 */
@Environment(EnvType.CLIENT)
@Data
public abstract class AbstractChatTab {

    // Each tab stores their own messages.
    public final List<AdvancedChatLine> messages = new ArrayList<>();
    public final List<AdvancedChatLine> visibleMessages = new ArrayList<>();
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
        this.visibleMessages.clear();

        for (int i = this.messages.size() - 1; i >= 0; --i) {
            AdvancedChatLine chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
        }

    }

    /**
     * Used for adding messages into the tab.
     *
     * @param text      Text to add.
     * @param messageId ID of message.
     * @param timestamp Amount of ticks when it was created.
     * @param bl        Add to messages
     */
    public void addMessage(Text text, int messageId, int timestamp, boolean bl) {
        addMessage(text, messageId, timestamp, bl, LocalTime.now());
    }

    public void addMessage(Text text, int messageId, int timestamp, boolean bl, LocalTime time) {
        if (!shouldAdd(text)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        AdvancedChatLine logLine = new AdvancedChatLine(timestamp, text, messageId, time);

        for (int i = 0; i < AdvancedChat.configStorage.chatStack && i < messages.size(); i++) {
            AdvancedChatLine chatLine = messages.get(i);
            if (text.getString().equals(chatLine.getText().getString())) {
                for (int j = 0; j < AdvancedChat.configStorage.chatStack + 15 && i < visibleMessages.size(); j++) {
                    AdvancedChatLine visibleLine = visibleMessages.get(j);
                    if (visibleLine.getUuid().equals(chatLine.getUuid())) {
                        visibleLine.setStacks(visibleLine.getStacks() + 1);
                        return;
                    }
                }

            }
        }

        boolean showtime = AdvancedChat.configStorage.chatConfig.showTime;
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(AdvancedChat.configStorage.timeFormat);
            SplitText split = new SplitText(text);
            split.addTime(format, time);
            text = split.getText();
        }

        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        int width = MathHelper.floor(AdvancedChatHud.getScaledWidth() - 5);

        for (MutableText newLine : MainChatTab.wrapText(client.textRenderer, width, text)) {

            this.visibleMessages.add(0, new AdvancedChatLine(timestamp, newLine, messageId, time, logLine.getUuid()));
        }

        hud.messageAddedToTab(this);

        int visibleMessagesMaxSize = AdvancedChat.configStorage.chatConfig.storedLines;
        while (this.visibleMessages.size() > visibleMessagesMaxSize) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!bl) {
            this.messages.add(0, logLine);

            while (this.messages.size() > visibleMessagesMaxSize) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void removeMessage(int messageId) {
        Iterator iterator = this.visibleMessages.iterator();

        AdvancedChatLine chatHudLine2;
        while (iterator.hasNext()) {
            chatHudLine2 = (AdvancedChatLine) iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
            }
        }

        iterator = this.messages.iterator();

        while (iterator.hasNext()) {
            chatHudLine2 = (AdvancedChatLine) iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
                break;
            }
        }
    }

}
