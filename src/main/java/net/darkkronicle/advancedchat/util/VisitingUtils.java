package net.darkkronicle.advancedchat.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
@Environment(EnvType.CLIENT)
public class VisitingUtils {

    public interface FormattingVisitable {
        boolean accept(char character, int index, int formattedIndex, Style style, Style formattedStyle);
    }

    public Text format(Text text) {
        SplitText t = new SplitText();
        visitFormatted(text, (c, index, formattedIndex, style, formattedStyle) -> {
            t.append(new SimpleText(String.valueOf(c), formattedStyle));
            return true;
        });
        return t.getText();
    }

    public boolean visitFormatted(StringVisitable text, FormattingVisitable visitor) {
        int length = text.getString().length();
        AtomicInteger currentIndex = new AtomicInteger();
        AtomicInteger realIndex = new AtomicInteger();
        AtomicReference<Style> currentStyle = new AtomicReference<>(Style.EMPTY);

        text.visit((textStyle, string) -> {
            if (!textStyle.equals(Style.EMPTY)) {
                currentStyle.set(textStyle);
            }
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                char nextChar;
                if (c == '§') {
                    if (currentIndex.get() + 1 >= length) {
                        break;
                    }

                    nextChar = string.charAt(i + 1);
                    Formatting formatting = Formatting.byCode(nextChar);
                    if (formatting != null) {
                        currentStyle.set(formatting == Formatting.RESET ? textStyle : textStyle.withExclusiveFormatting(formatting));
                        if (currentStyle.get().equals(Style.EMPTY)) {
                            currentStyle.set(textStyle);
                        }
                    }
                    currentIndex.getAndIncrement();
                    i++;
                } else if (Character.isHighSurrogate(c)) {
                    if (currentIndex.get() + 1 >= length) {
                        if (!visitor.accept((char) 65533, currentIndex.get(), realIndex.get(), textStyle, currentStyle.get())) {
                            return Optional.of(StringVisitable.TERMINATE_VISIT);
                        }
                        break;
                    }

                    nextChar = string.charAt(i + 1);
                    if (Character.isLowSurrogate(nextChar)) {
                        if (!visitor.accept((char) Character.toCodePoint(c, nextChar), currentIndex.get(), realIndex.get(), textStyle, currentStyle.get())) {
                            return Optional.of(StringVisitable.TERMINATE_VISIT);
                        }
                        realIndex.getAndIncrement();
                        currentIndex.getAndIncrement();
                        ++i;
                    } else if (!visitor.accept((char) 65533, currentIndex.get(), realIndex.get(), textStyle, currentStyle.get())) {
                        return Optional.of(StringVisitable.TERMINATE_VISIT);
                    }
                    realIndex.getAndIncrement();
                } else if (Character.isSurrogate(c) ? visitor.accept((char) 65533, currentIndex.get(), realIndex.get(), textStyle, currentStyle.get()) : visitor.accept(c, currentIndex.get(), realIndex.get(), textStyle, currentStyle.get())) {
                    realIndex.getAndIncrement();
                } else {
                    return Optional.of(StringVisitable.TERMINATE_VISIT);
                }
                currentIndex.getAndIncrement();
            }
            return Optional.empty();
        }, Style.EMPTY);


        return true;
    }

}
