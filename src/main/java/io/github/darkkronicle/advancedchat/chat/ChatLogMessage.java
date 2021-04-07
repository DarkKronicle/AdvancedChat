package io.github.darkkronicle.advancedchat.chat;

import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@EqualsAndHashCode(callSuper = true)
@Environment(EnvType.CLIENT)
@Data
public class ChatLogMessage extends ChatMessage {
    private AbstractChatTab[] tab;

    public ChatLogMessage(ChatMessage message, AbstractChatTab... tabs) {
        super(message.creationTick, message.displayText, message.originalText, message.id, message.time, message.background, 600, message.owner);
        this.tab = tabs;
    }

}
