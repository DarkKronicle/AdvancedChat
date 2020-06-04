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

package darkkronicle.advancedchat.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class FormattedText {

    /*
    Class to convert plain old strings into formatted Text. This is so that minecraft can
    handle line breaks correctly and keep formatting.
     */

    public static Text formatText(String string) {
        Text formatted = new LiteralText("");
        StringBuilder stringBuilder = new StringBuilder();
        Style style = new Style();
        style.setColor(Formatting.WHITE);
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (current == '§') {
                if (stringBuilder.length() != 0) {
                    Text text = new LiteralText(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    text.setStyle(style);
                    formatted.append(text);
                }
                i++;
                Formatting color = Formatting.byCode(chars[i]);
                if (color.isColor()) {
                    style = new Style();
                    style.setColor(color);
                } else {
                    switch (color) {
                        case BOLD:
                            style.setBold(true);
                            break;
                        case ITALIC:
                            style.setItalic(true);
                            break;
                        case OBFUSCATED:
                            style.setObfuscated(true);
                            break;
                        case UNDERLINE:
                            style.setUnderline(true);
                            break;
                        case STRIKETHROUGH:
                            style.setStrikethrough(true);
                            break;
                    }
                }
            } else {
                stringBuilder.append(current);
            }
        }
        if (stringBuilder.length() != 0) {
            Text text = new LiteralText(stringBuilder.toString());
            stringBuilder.setLength(0);
            text.setStyle(style);
            formatted.append(text);
        }
        return formatted;
    }

}
