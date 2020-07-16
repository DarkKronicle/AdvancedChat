package net.darkkronicle.advancedchat.gui.tabs;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Class to manage different tabs used in AdvancedChatHud
 */
@Environment(EnvType.CLIENT)
@Data
public abstract class AbstractChatTab {
    public final List<AdvancedChatLine> messages = new ArrayList<>();
    public final List<AdvancedChatLine> visibleMessages = new ArrayList<>();
    private final String name;
    private final AdvancedChatHud hud;
    private final MinecraftClient client;

    public AbstractChatTab(String name) {
        hud = AdvancedChat.getAdvancedChatHud();
        client = MinecraftClient.getInstance();
        this.name = name;
    }

    public abstract boolean shouldAdd(StringRenderable stringRenderable);

    public void reset() {
        this.visibleMessages.clear();

        for(int i = this.messages.size() - 1; i >= 0; --i) {
            AdvancedChatLine chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
        }

    }

    public void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl) {
        if (!shouldAdd(stringRenderable)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        Optional<StringRenderable> filtered = AdvancedChat.filter.filter(stringRenderable);
        if (filtered.isPresent()) {
            stringRenderable = filtered.get();
        }

        int width = MathHelper.floor((double)hud.getWidth() / hud.getChatScale());
        List<StringRenderable> list = ChatMessages.breakRenderedChatMessageLines(stringRenderable, width, this.client.textRenderer);

        StringRenderable stringRenderable2;
        for(Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, new AdvancedChatLine(timestamp, stringRenderable2, messageId))) {
            stringRenderable2 = (StringRenderable)var8.next();
            hud.messageAddedToTab(this);
        }

        int visibleMessagesMaxSize = 500;
        while(this.visibleMessages.size() > visibleMessagesMaxSize) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!bl) {
            this.messages.add(0, new AdvancedChatLine(timestamp, stringRenderable, messageId));

            while(this.messages.size() > visibleMessagesMaxSize) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void removeMessage(int messageId) {
        Iterator iterator = this.visibleMessages.iterator();

        ChatHudLine chatHudLine2;
        while(iterator.hasNext()) {
            chatHudLine2 = (ChatHudLine)iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
            }
        }

        iterator = this.messages.iterator();

        while(iterator.hasNext()) {
            chatHudLine2 = (ChatHudLine)iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
                break;
            }
        }
    }

}
