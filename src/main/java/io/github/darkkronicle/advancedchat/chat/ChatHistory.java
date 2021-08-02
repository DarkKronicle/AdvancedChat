package io.github.darkkronicle.advancedchat.chat;

import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to maintain the storage of the chat.
 */
@Environment(EnvType.CLIENT)
public class ChatHistory {

    private final static ChatHistory INSTANCE = new ChatHistory();

    @Getter
    private final List<ChatMessage> messages = new ArrayList<>();

    public static ChatHistory getInstance() {
        return INSTANCE;
    }

    private ChatHistory() {

    }

    /**
     * Clear's all the chat messages from the history
     */
    public void clear() {
        messages.clear();
    }

    /**
     * Add's a chat message to the history. This forwards the new message to {@link AdvancedChatHud} as well.
     * @param message
     */
    public void add(ChatMessage message) {
        for (int i = 0; i < ConfigStorage.General.CHAT_STACK.config.getIntegerValue() && i < messages.size(); i++) {
            ChatMessage chatLine = messages.get(i);
            if (message.isSimilar(chatLine)) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }
        messages.add(0, message);
        while (this.messages.size() > ConfigStorage.ChatLog.STORED_LINES.config.getIntegerValue()) {
            this.messages.remove(this.messages.size() - 1);
        }
        AdvancedChatHud.getInstance().onNewMessage(message);
    }

    /**
     * Remove's a message based off of it's messageId.
     * @param messageId Message ID to find and remove
     */
    public void removeMessage(int messageId) {
        this.messages.removeIf(line -> line.getId() == messageId);
    }

}
