package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

/*
Class that allows for easy mutable objects that are like minecraft Text.
 */

@Accessors(chain = true)
@AllArgsConstructor
@Environment(EnvType.CLIENT)
public class SimpleText {
    @Getter @Setter @With
    private String message;
    @Getter @Setter @With
    private Style style;

    private SimpleText(SimpleText text) {
        this.style = text.withStyle(text.getStyle()).getStyle();
        this.message = text.getMessage();
    }

    // Defensive copy of simpleText.
    public SimpleText copySimpleText() {
        return new SimpleText(this);
    }

    public static SimpleText withColor(String string, ColorUtil.SimpleColor color) {
        if (color == null) {
            return new SimpleText(string, Style.EMPTY);
        }
        Style style = Style.EMPTY;
        TextColor textColor = TextColor.fromRgb(color.color());
        return SimpleText.withStyle(string, style.withColor(textColor));
    }

    public static SimpleText withStyle(String string, Style base) {
        return new SimpleText(string, base);
    }

}
