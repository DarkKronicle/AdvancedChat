/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

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
        TextColor textColor = TextColor.fromRgb(color.color());
        return SimpleText.withStyle(string, Style.EMPTY.withColor(textColor));
    }

    public static SimpleText withStyle(String string, Style base) {
        return new SimpleText(string, base);
    }

}
