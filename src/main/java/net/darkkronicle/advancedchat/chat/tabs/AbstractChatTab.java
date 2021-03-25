package net.darkkronicle.advancedchat.chat.tabs;

import lombok.Data;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.chat.AdvancedChatMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * Method to reformat the messages if General size changes or something.
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

    public void addMessage(AdvancedChatMessage line) {
        if (!shouldAdd(line.getText())) {
            return;
        }
        // Whether or not to override one
        if (line.getId() != 0) {
            this.removeMessage(line.getId());
        }
        for (int i = 0; i < ConfigStorage.General.CHAT_STACK.config.getIntegerValue() && i < messages.size(); i++) {
            AdvancedChatMessage chatLine = messages.get(i);
            if (line.isSimilar(chatLine)) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }

        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        this.messages.add(0, line);
        hud.messageAddedToTab(this);
        int visibleMessagesMaxSize = ConfigStorage.ChatScreen.STORED_LINES.config.getIntegerValue();
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
