package darkkronicle.advancedchat.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FormattedText extends BaseText {
    private List<Text> siblings = new ArrayList<>();
    private String string;

    public FormattedText(String string) {
        this.string = string;
    }

    public String getRawString() {
        return this.string;
    }

    public String asString() {
        return this.string;
    }

    public FormattedText copy() {
        return new FormattedText(string);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LiteralText)) {
            return false;
        } else {
            LiteralText literalText = (LiteralText) o;
            return this.string.equals(literalText.getRawString()) && super.equals(o);
        }
    }

    public String toString() {
        return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public List<Text> formatText(String string) {
        List<Text> formatted = new ArrayList<>();
        String[] sections = string.split("§");
        for (String section : sections) {
            Text text = new LiteralText(section.substring(2));
            text.setStyle(new Style().setColor(Formatting.byCode(section.charAt(0))));
            formatted.add(text);
        }
        return formatted;
    }

    public String asFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = "";

        for (Text text : siblings) {
            String string2 = text.asString();
            if (!string2.isEmpty()) {
                String string3 = text.getStyle().asString();
                if (!string3.equals(string)) {
                    if (!string.isEmpty()) {
                        stringBuilder.append(Formatting.RESET);
                    }

                    stringBuilder.append(string3);
                    string = string3;
                }

                stringBuilder.append(string2);
            }
        }

        if (!string.isEmpty()) {
            stringBuilder.append(Formatting.RESET);
        }

        return stringBuilder.toString();
    }

}
