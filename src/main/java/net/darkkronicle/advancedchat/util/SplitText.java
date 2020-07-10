package net.darkkronicle.advancedchat.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h1>SplitText</h1>
 * A helper class that can take a StringRenderable, break it up, and put it back together.
 * This breaks up the StringRenderable into different {@link SimpleText}.
 * This allows for easy editing of text and can modify it in {@link net.darkkronicle.advancedchat.filters.ReplaceFilter}
 */
@Environment(EnvType.CLIENT)
public class SplitText {

    private ArrayList<SimpleText> siblings = new ArrayList<>();

    /**
     * <h1>SplitText</h1>
     * Takes a stringRenderable and splits it into a list of {@link SimpleText}.
     *
     * @param stringRenderable StringRenderable to split into different {@link SimpleText}
     */
    public SplitText(StringRenderable stringRenderable) {
        stringRenderable.visit((style, string) -> {
            siblings.add(new SimpleText(string, style));
            return Optional.empty();
        }, Style.EMPTY);
    }

    /**
     * <h1>copyStyle</h1>
     * Used to do a defensive copy of minecraft Style.
     *
     * @param style Style to copy.
     * @return Style that is a copy of imputed parameter.
     */
    public static Style copyStyle(Style style) {
        Style copy = Style.EMPTY;
        copy.withColor(style.getColor());
        if (style.isItalic()) {
            copy.withItalic(true);
        }
        if (style.isUnderlined()) {
            copy.withFormatting(Formatting.UNDERLINE);
        }
        if (style.isStrikethrough()) {
            copy.withFormatting(Formatting.STRIKETHROUGH);
        }
        if (style.isObfuscated()) {
            copy.withFormatting(Formatting.OBFUSCATED);
        }
        if (style.getClickEvent() != null) {
            ClickEvent click = style.getClickEvent();
            copy.withClickEvent(click);
        }
        if (style.getHoverEvent() != null) {
            HoverEvent hover = style.getHoverEvent();
            copy.setHoverEvent(hover);
        }
        if (style.getInsertion() != null) {
            copy.withInsertion(style.getInsertion());
        }
        copy.withFont(style.getFont());
        return copy;
    }

    /**
     * <h1>getFullMessage</h1>
     * Takes the SplitText that is stored inside of this class, and puts it into a plain string.
     * Used mainly for debugging and {@link SearchText}
     *
     * @return Plain string of just the raw text of held {@link SimpleText}
     */
    public String getFullMessage() {
        if (siblings.size() == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (SimpleText text : getSiblings()) {
            stringBuilder.append(text.getMessage());
        }
        return stringBuilder.toString();
    }

    /**
     * <h1>getStringRenderable</h1>
     * Links stored SimpleText into a StringRenderable that is then returned.
     * After mutating text in here it can be brought back to a minecraft friendly
     * object.
     *
     * @return StringRenderable that is composed of all the {@link SimpleText}
     */
    public StringRenderable getStringRenderable() {
        TextCollector textCollector = new TextCollector();
        for (SimpleText text : getSiblings()) {
            textCollector.add(StringRenderable.styled(text.getMessage(), text.getStyle()));
        }
        return textCollector.getCombined();
    }

    /**
     * <h1>replaceStrings</h1>
     * Complex method used to split up the split text in this class and replace matches to a string.
     *
     * @param matches List of {@link net.darkkronicle.advancedchat.util.SearchText.StringMatch} to replace.
     * @param replace String to replace the matches to.
     */
    public void replaceStrings(List<SearchText.StringMatch> matches, String replace) {
        replaceStrings(matches, new SimpleText(replace, Style.EMPTY));
    }

    /**
     * <h1>replaceStrings</h1>
     * Complex method used to split up the split text in this class and replace matches to a string.
     *
     * @param matches List of {@link net.darkkronicle.advancedchat.util.SearchText.StringMatch} to replace.
     * @param replace {@link SimpleText} to replace the matches to.
     */
    public void replaceStrings(List<SearchText.StringMatch> matches, SimpleText replace) {
        // List of new SimpleText to form a new SplitText.
        ArrayList<SimpleText> newSiblings = new ArrayList<>();
        // int used to remember where the match stopped before.
        int stopped = 0;
        // What match this is currently on.
        int matchnum = 0;
        for (SearchText.StringMatch match : matches) {
            matchnum++;
            // Text that includes the start of the match.
            SimpleText startedText = null;
            // Char num of where the match started in the text.
            int startedInt;
            // Text that includes the end of the match.
            SimpleText endedText = null;
            // Char num where the match ended in the text.
            int endedInt;
            // Total number of chars went through. Used to find where the match end and beginning is.
            int totalchar = 0;
            // If end text has been found.
            boolean ended = false;
            // If start text have been found.
            boolean started = false;
            for (SimpleText text : getSiblings()) {
                if (text.getMessage() == null || text.getMessage().length() <= 0) {
                    continue;
                }

                int length = text.getMessage().length();

                // Boolean to check to see if the SimpleText was modified.
                boolean modified = false;

                // Checks to see if current text contains the match.start.
                if (totalchar + length > match.start && !started) {
                    startedText = text;
                    startedInt = match.start - totalchar;
                    // Used for multiple matches. Once the second match starts, it starts at the beginning. We use this to go to where the last match went to.
                    int togo = 0;
                    if (stopped != 0 && totalchar < stopped && totalchar + length > stopped) {
                        togo = stopped - totalchar;
                    }
                    // Splits the text from the beginning to the match. Used to easily edit the Style.
                    newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(togo, startedInt)));
                    if (replace.getStyle().equals(Style.EMPTY)) {
                        newSiblings.add(text.copySimpleText().setMessage(replace.getMessage()));
                    } else {
                        newSiblings.add(replace);
                    }
                    started = true;
                    modified = true;
                }

                // Checks to see if current text contains the match.end.
                if (length + totalchar >= match.end && !ended) {
                    endedText = text;
                    endedInt = match.end - totalchar;
                    // If there are more matches, stop adding to newSiblings
                    if (matches.size() > matchnum) {
                        stopped = match.end;
                        continue;
                    }
                    newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(endedInt)));
                    ended = true;
                    modified = true;

                }

                // If we're in the middle of the start and the end text, we flag this text to not be added to newSiblings.
                if (started && !ended) {
                    modified = true;
                }

                if (!modified) {
                    if (stopped != 0 && totalchar < stopped && totalchar + length > stopped) {
                        // Used for multiple matches.
                        newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(stopped - totalchar)));
                    } else if (stopped != 0 && stopped <= totalchar) {
                        newSiblings.add(text);
                    }
                }

                totalchar = totalchar + length;

            }

            if (startedText == null || endedText == null) {
                // Because we know that there are already matches, we should have replace them.
                // TODO log this correctly.
                System.out.println("Something went wrong!");
                return;
            }

        }
        // At the end we take the siblings created in this method and override the old ones.
        siblings = newSiblings;

    }

    public List<SimpleText> getSiblings() {
        return siblings;
    }

}
