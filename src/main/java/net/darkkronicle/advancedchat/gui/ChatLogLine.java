package net.darkkronicle.advancedchat.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Value
public class ChatLogLine {
    StringRenderable text;
    int id;
    AbstractChatTab[] tab;
    @NonFinal
    LocalTime time = LocalTime.now();

}
