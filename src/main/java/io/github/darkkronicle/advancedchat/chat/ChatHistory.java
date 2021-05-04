package io.github.darkkronicle.advancedchat.chat;

import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

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

    public void clear() {
        messages.clear();
    }

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

    public void removeMessage(int messageId) {
        this.messages.removeIf(line -> line.getId() == messageId);
    }

}
