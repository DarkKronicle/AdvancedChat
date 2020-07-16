package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

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
        this.style = SplitText.copyStyle(text.getStyle());
        this.message = text.getMessage();
    }

    // Defensive copy of simpleText.
    public SimpleText copySimpleText() {
        return new SimpleText(this);
    }


}
