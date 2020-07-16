package net.darkkronicle.advancedchat.gui;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
@Data
@Value
public class AdvancedChatLine {
    int creationTick;
    StringRenderable text;
    int id;
    LocalTime time = LocalTime.now();
    @NonFinal
    ColorUtil.SimpleColor background;

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, ColorUtil.SimpleColor background) {
        this.creationTick = creationTick;
        this.text = text;
        this.id = id;
        this.background = background;
    }



}
