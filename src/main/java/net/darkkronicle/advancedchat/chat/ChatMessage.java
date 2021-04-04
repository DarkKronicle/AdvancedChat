package net.darkkronicle.advancedchat.chat;

import lombok.Builder;
import lombok.Data;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.StyleFormatter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Data
public class ChatMessage {
    protected int creationTick;
    protected Text displayText;
    protected Text originalText;
    protected int id;
    protected LocalTime time;
    protected ColorUtil.SimpleColor background;
    protected int stacks;
    protected UUID uuid;
    protected MessageOwner owner;
    protected ArrayList<AdvancedChatLine> lines;

    public void setDisplayText(Text text, int width) {
        this.displayText = text;
        formatChildren(width);
    }

    public ChatMessage shallowClone(int width) {
        return new ChatMessage(creationTick, displayText, originalText, id, time, background, width, owner);
    }

    @Data
    public static class AdvancedChatLine {
        private Text text;
        private final ChatMessage parent;
        private int width;

        private AdvancedChatLine(ChatMessage parent, Text text) {
            this.parent = parent;
            this.text = text;
            this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
        }
    }

    @Builder
    protected ChatMessage(int creationTick, Text displayText, Text originalText, int id, LocalTime time, ColorUtil.SimpleColor background, int width, MessageOwner owner) {
        this.creationTick = creationTick;
        this.displayText = displayText;
        this.id = id;
        this.time = time;
        this.background = background;
        this.stacks = 0;
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.originalText = originalText == null ? displayText : originalText;
        formatChildren(width);
    }

    public void formatChildren(int width) {
        this.lines = new ArrayList<>();
        if (width == 0) {
            this.lines.add(new AdvancedChatLine(this, displayText));
        } else {
            for (Text t : StyleFormatter.wrapText(MinecraftClient.getInstance().textRenderer, width, displayText)) {
                this.lines.add(new AdvancedChatLine(this, t));
            }
        }
    }

    public boolean isSimilar(ChatMessage message) {
        return message.getOriginalText().getString().equals(this.getOriginalText().getString());
    }

    public int getLineCount() {
        return this.lines.size();
    }

}
