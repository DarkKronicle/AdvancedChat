package net.darkkronicle.advancedchat.gui;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
@Data
@Value
public class ChatLogLine {
    StringRenderable text;
    int id;
    LocalTime time = LocalTime.now();
    AbstractChatTab[] tab;
}
