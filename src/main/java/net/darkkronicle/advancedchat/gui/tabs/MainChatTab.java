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

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatLine;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main chat tab that manages other chat tabs.
 */
public class MainChatTab extends AbstractChatTab {
    @Getter
    private ArrayList<AbstractChatTab> allChatTabs = new ArrayList<>();
    @Getter
    private ArrayList<CustomChatTab> customChatTabs = new ArrayList<>();

    public MainChatTab() {
        super("Main", "Main");
        setUpTabs();
    }

    @Override
    public boolean shouldAdd(Text text) {
        return true;
    }

    @Override
    public void addMessage(Text text, int messageId, int timestamp, boolean bl, LocalTime time) {
        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();
        MinecraftClient client = MinecraftClient.getInstance();
        Text unfiltered = text;
        Optional<Text> filtered = AdvancedChat.filter.filter(text);
        if (filtered.isPresent()) {
            text = filtered.get();
        }

        if (text.getString().length() <= 0) {
            return;
        }
        ColorUtil.SimpleColor backcolor = null;
        for (ColorFilter colorFilter : AdvancedChat.filter.getColorFilters()) {
            backcolor = colorFilter.getBackgroundColor(text);
            if (backcolor != null) {
                break;
            }
        }
        // Goes through chat tabs
        boolean dontforward = false;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (customChatTabs.size() > 0) {
            for (CustomChatTab tab : customChatTabs) {
                if (tab.shouldAdd(text)) {
                    tab.addMessage(text, messageId, timestamp, bl);
                    added.add(tab);
                    if (!tab.isForward()) {
                        dontforward = true;
                        break;
                    }
                }
            }
        }
        if (!dontforward) {
            added.add(this);
        }
        AdvancedChat.getChatLogData().addMessage(unfiltered, added.toArray(new AbstractChatTab[0]));
        if (dontforward) {
            return;
        }
        if (!shouldAdd(text)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }


        Text logged = text;
        AdvancedChatLine logLine = new AdvancedChatLine(timestamp, logged, messageId, time);

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

        for (int i = 0; i < AdvancedChat.configStorage.chatStack && i < visibleMessages.size(); i++) {
            AdvancedChatLine chatLine = visibleMessages.get(i);
            if (text.getString().equals(chatLine.getText().getString())) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }

        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        int width = MathHelper.floor(AdvancedChatHud.getWidth() - 5 );

        for (OrderedText breakRenderedChatMessageLine : ChatMessages.breakRenderedChatMessageLines(text, width, client.textRenderer)) {
            MutableText newLine = new LiteralText("");

            AtomicReference <Style> oldStyle = new AtomicReference<>(null);
            AtomicReference<String> s = new AtomicReference<>("");

            breakRenderedChatMessageLine.accept((index, style, codePoint) -> {
                if (oldStyle.get() == null) {
                    oldStyle.set(style);
                }

                if (oldStyle.get() != style) {
                    newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
                    oldStyle.set(style);
                    s.set("");
                }

                s.set(s.get() + (char) codePoint);
                return true;
            });

            if (!s.get().isEmpty()) {
                newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
            }

            this.visibleMessages.add(0, new AdvancedChatLine(timestamp, newLine, messageId, time, backcolor, 0, logLine.getUuid()));
        }

        hud.messageAddedToTab(this);

        int visibleMessagesMaxSize = AdvancedChat.configStorage.chatConfig.storedLines;
        while(this.visibleMessages.size() > visibleMessagesMaxSize) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!bl) {
            this.messages.add(0, logLine);

            while(this.messages.size() > visibleMessagesMaxSize) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    /**
     * Method used for loading in tabs from the config.
     */
    public void setUpTabs() {
        customChatTabs = new ArrayList<>();
        allChatTabs = new ArrayList<>();
        allChatTabs.add(this);
        for (ChatTab tab : AdvancedChat.configStorage.tabs) {
            CustomChatTab customTab = new CustomChatTab(tab.getName(), tab.getAbreviation(), tab.getFindType(), tab.getFindString(), tab.isForward(), tab.getStartingMessage());
            customChatTabs.add(customTab);
            allChatTabs.add(customTab);
        }
    }

}
