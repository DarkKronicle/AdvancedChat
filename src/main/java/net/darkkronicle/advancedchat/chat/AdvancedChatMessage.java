package net.darkkronicle.advancedchat.chat;

import lombok.Builder;
import lombok.Data;
import net.darkkronicle.advancedchat.gui.MessageOwner;
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
public class AdvancedChatMessage {
    private int creationTick;
    private Text text;
    private Text rawText;
    private int id;
    private LocalTime time;
    private ColorUtil.SimpleColor background;
    private int stacks;
    private UUID uuid;
    private MessageOwner owner;
    private ArrayList<AdvancedChatLine> lines;

    @Data
    public static class AdvancedChatLine {
        private Text text;
        private final AdvancedChatMessage parent;
        private int width;

        private AdvancedChatLine(AdvancedChatMessage parent, Text text) {
            this.parent = parent;
            this.text = text;
            this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
        }
    }

    @Builder
    private AdvancedChatMessage(int creationTick, Text text, Text originalText, int id, LocalTime time, ColorUtil.SimpleColor background, int width, MessageOwner owner) {
        this.creationTick = creationTick;
        this.text = text;
        this.id = id;
        this.time = time;
        this.background = background;
        this.stacks = 0;
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.rawText = originalText == null ? text : originalText;
        formatChildren(width);
    }

    public void formatChildren(int width) {
        this.lines = new ArrayList<>();
        if (width == 0) {
            this.lines.add(new AdvancedChatLine(this, text));
        } else {
            for (Text t : StyleFormatter.wrapText(MinecraftClient.getInstance().textRenderer, width, text)) {
                this.lines.add(new AdvancedChatLine(this, t));
            }
        }
    }

    public boolean isSimilar(AdvancedChatMessage message) {
        return message.getRawText().getString().equals(this.getRawText().getString());
    }

    public int getLineCount() {
        return this.lines.size();
    }

}
