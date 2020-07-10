package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@Accessors(chain = true)
@AllArgsConstructor
@Environment(EnvType.CLIENT)
public class SimpleText {
    @Getter @Setter
    private String message;
    @Getter @Setter
    private Style style;

    private SimpleText(SimpleText text) {
        this.style = SplitText.copyStyle(text.getStyle());
        this.message = text.getMessage();
    }

    public SimpleText copySimpleText() {
        return new SimpleText(this);
    }


}
