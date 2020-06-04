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
