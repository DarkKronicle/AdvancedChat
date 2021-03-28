package net.darkkronicle.advancedchat.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to format text without losing data
 */
@Environment(EnvType.CLIENT)
public class StyleFormatter {

    /**
     * An interface to take multiple inputs from a string that has the Section Symbol formatting
     * combined with standard {@link Text} formatting.
     */
    public interface FormattingVisitable {

        /**
         * Accepts a character with information about current formatting
         *
         * @param c Current character
         * @param currentIndex The current index of the raw string
         * @param realIndex The current index without formatting symbols
         * @param textStyle The style that the text currently has
         * @param formattingStyle The style that combines text formatting and formatting symbols
         * @return Whether to continue
         */
        boolean accept(char c, int currentIndex, int realIndex, Style textStyle, Style formattingStyle);
    }

    private int currentIndex;
    private int realIndex;
    private Style currentStyle;
    private final FormattingVisitable visitor;
    private final int length;

    /**
     * Results of different parts of formatting
     */
    private enum Result {
        /**
         * Go up a character
         */
        INCREMENT,

        /**
         * It worked!
         */
        SUCCESS,

        /**
         * Go to the next {@link StringVisitable}
         */
        SKIP,

        /**
         * STOP
         */
        TERMINATE
    }

    /**
     * Creates a StyleFormatter for a given length and a {@link FormattingVisitable}
     *
     * This class is meant to be updated with a {@link StringVisitable.StyledVisitor}
     *
     * @param visitor {@link FormattingVisitable} to get updated with each visible character
     * @param length Length of the string
     */
    public StyleFormatter(FormattingVisitable visitor, int length) {
        this.visitor = visitor;
        this.currentIndex = 0;
        this.realIndex = 0;
        this.currentStyle = Style.EMPTY;
        this.length = length;
    }

    /**
     * Sends the visitor the maximum character. Used for high surrogate characters.
     */
    private boolean sendUnsupportedToVisitor(Style textStyle) {
        return sendToVisitor((char) 65533, textStyle);
    }

    /**
     * Sends the current character with the current information to the visitor.
     */
    private boolean sendToVisitor(char c, Style textStyle) {
        return visitor.accept(c, currentIndex, realIndex, textStyle, currentStyle);
    }

    /**
     * Handles how section symbols get processed
     */
    private Result updateSection(Style textStyle, Character nextChar) {
        if (nextChar == null) {
            return Result.SKIP;
        }
        Formatting formatting = Formatting.byCode(nextChar);
        if (formatting != null) {
            currentStyle = formatting == Formatting.RESET ? textStyle : textStyle.withExclusiveFormatting(formatting);
            if (currentStyle.equals(Style.EMPTY)) {
                currentStyle = textStyle;
            }
        }
        currentIndex++;
        return Result.INCREMENT;
    }

    /**
     * If a character is a high surrogate, it goes to here to get updated.
     */
    private Result updateHighSurrogate(Style textStyle, char c, Character nextChar) {
        if (nextChar == null) {
            if (!sendUnsupportedToVisitor(textStyle)) {
                return Result.TERMINATE;
            }
            return Result.SKIP;
        }

        if (Character.isLowSurrogate(nextChar)) {
            if (!sendToVisitor((char) Character.toCodePoint(c, nextChar), textStyle)) {
                return Result.TERMINATE;
            }
            realIndex += 2;
            currentIndex++;
            return Result.INCREMENT;
        } else if (!sendUnsupportedToVisitor(textStyle)) {
            return Result.TERMINATE;
        }
        realIndex++;
        return Result.SUCCESS;
    }

    /**
     * Updates current visitable data as well as signifies whether to end.
     *
     * Calling this method will result in each 'visible' character being sent to the {@link FormattingVisitable}
     *
     * @param textStyle Style of the current string
     * @param string The current string
     * @return Value to terminate. Follows {@link StringVisitable.StyledVisitor} return values.
     */
    public Optional<Optional<Unit>> updateStyle(Style textStyle, String string) {
        if (!textStyle.equals(Style.EMPTY)) {
            currentStyle = textStyle;
        }
        int stringLength = string.length();
        for (int i = 0; i < stringLength; i++) {
            char c = string.charAt(i);
            Character nextChar = null;
            if (i + 1 < stringLength) {
                nextChar = string.charAt(i + 1);
            }
            if (c == '§') {
                switch (updateSection(textStyle, nextChar)) {
                    case SKIP:
                        return Optional.empty();
                    case TERMINATE:
                        return Optional.of(StringVisitable.TERMINATE_VISIT);
                    case INCREMENT:
                        i++;
                }
            } else if (Character.isHighSurrogate(c)) {
                switch (updateHighSurrogate(textStyle, c, nextChar)) {
                    case SKIP:
                        return Optional.empty();
                    case TERMINATE:
                        return Optional.of(StringVisitable.TERMINATE_VISIT);
                    case INCREMENT:
                        i++;
                }
            } else if (Character.isSurrogate(c) ? sendUnsupportedToVisitor(textStyle) : sendToVisitor(c, textStyle)) {
                realIndex++;
            } else {
                return Optional.of(StringVisitable.TERMINATE_VISIT);
            }
            currentIndex++;
        }
        return Optional.empty();
    }

    /**
     * Formats text that contains styling data as well as formatting symbols
     *
     * This method is used to remove section symbols while maintaining previous formatting
     * as well as new formatting.
     *
     * @param text Text to reformat
     * @return Formatted text
     */
    public static Text formatText(Text text) {
        SplitText t = new SplitText();
        int length = new SplitText(text).getFullMessage().length();
        StyleFormatter formatter = new StyleFormatter((c, index, formattedIndex, style, formattedStyle) -> {
            t.append(new SimpleText(String.valueOf(c), formattedStyle), false);
            return true;
        }, length);
        text.visit(formatter::updateStyle, Style.EMPTY);
        return t.getText();
    }

    /**
     * Wraps text into multiple lines
     *
     * @param textRenderer TextRenderer to handle text
     * @param scaledWidth Maximum width before the line breaks
     * @param text Text to break up
     * @return List of MutableText of the new lines
     */
    public static List<MutableText> wrapText(TextRenderer textRenderer, int scaledWidth, Text text) {
        ArrayList<MutableText> lines = new ArrayList<>();
        for (OrderedText breakRenderedChatMessageLine : ChatMessages.breakRenderedChatMessageLines(text, scaledWidth, textRenderer)) {
            MutableText newLine = new LiteralText("");

            AtomicReference<Style> oldStyle = new AtomicReference<>(null);
            AtomicReference<String> s = new AtomicReference<>("");

            breakRenderedChatMessageLine.accept((index, style, codePoint) -> {
                if (oldStyle.get() == null) {
                    oldStyle.set(style);
                }

                if (oldStyle.get() != style) {
                    newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
                    oldStyle.set(style);
                    s.set("");
                }

                s.set(s.get() + (char) codePoint);
                return true;
            });

            if (!s.get().isEmpty()) {
                newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
            }
            lines.add(newLine);
        }
        return lines;
    }

}