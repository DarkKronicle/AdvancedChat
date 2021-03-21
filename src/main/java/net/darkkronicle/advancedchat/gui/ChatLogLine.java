package net.darkkronicle.advancedchat.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ChatLogLine {
    private Text text;
    private int id;
    private AbstractChatTab[] tab;
    private LocalTime time = LocalTime.now();
    private UUID uuid;

    public ChatLogLine(Text text, int id, AbstractChatTab... tab) {
        this.tab = tab;
        this.id = id;
        this.text = text;
        this.uuid = UUID.randomUUID();
    }

    public ChatLogLine(Text text, int id, LocalTime time, UUID uuid, AbstractChatTab... tab) {
        this.tab = tab;
        this.id = id;
        this.text = text;
        this.uuid = uuid;
        this.time = time;
    }

    public ChatLogLine(Text text, int id, AbstractChatTab[] tab, LocalTime time) {
        this.text = text;
        this.tab = tab;
        this.id = id;
        this.time = time;
        this.uuid = UUID.randomUUID();
    }
}
