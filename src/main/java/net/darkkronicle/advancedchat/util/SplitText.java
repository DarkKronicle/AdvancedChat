package net.darkkronicle.advancedchat.util;

import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * A helper class that can take a Text, break it up, and put it back together.
 * This breaks up the Text into different {@link SimpleText}.
 * This allows for easy editing of text and can modify it in {@link net.darkkronicle.advancedchat.filters.ReplaceFilter}
 */
@Environment(EnvType.CLIENT)
public class SplitText {

    private ArrayList<SimpleText> siblings = new ArrayList<>();

    /**
     * Takes a Text and splits it into a list of {@link SimpleText}.
     *
     * @param text text to split into different {@link SimpleText}
     */
    public SplitText(Text text) {
        text.visit((style, string) -> {
            siblings.add(new SimpleText(string, style));
            return Optional.empty();
        }, Style.EMPTY);
    }

    public SplitText() {

    }

    public SplitText(SimpleText base) {
        siblings.add(base);
    }

    public SplitText(List<SimpleText> siblings) {
        this.siblings.addAll(siblings);
    }


    /**
     * Takes the SplitText that is stored inside of this class, and puts it into a plain string.
     * Used mainly for debugging and {@link SearchUtils}
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
     * Links stored SimpleText into a Text that is then returned.
     * After mutating text in here it can be brought back to a minecraft friendly
     * object.
     *
     * @return Text that is composed of all the {@link SimpleText}
     */
    public Text getText() {
        LiteralText t = new LiteralText("");
        for (SimpleText text : getSiblings()) {
            t.append(new LiteralText(text.getMessage()).setStyle(text.getStyle()));
        }
        return t;
    }

    public SplitText truncate(SearchUtils.StringMatch match) {
        ArrayList<SimpleText> newSiblings = new ArrayList<>();
        boolean start = false;
        // Total number of chars went through. Used to find where the match end and beginning is.
        int totalchar = 0;
        for (SimpleText text : getSiblings()) {
            if (text.getMessage() == null || text.getMessage().length() <= 0) {
                continue;
            }

            int length = text.getMessage().length();

            // Checks to see if current text contains the match.start.
            if (totalchar + length > match.start) {
                if (totalchar + length >= match.end) {
                    if (!start) {
                        newSiblings.add(text.withMessage(text.getMessage().substring(match.start - totalchar, match.end - totalchar)));
                    } else {
                        newSiblings.add(text.withMessage(text.getMessage().substring(0, match.end - totalchar)));
                    }
                    return new SplitText(newSiblings);
                } else {
                    if (!start) {
                        newSiblings.add(text.withMessage(text.getMessage().substring(match.start - totalchar)));
                        start = true;
                    } else {
                        newSiblings.add(text);
                    }
                }
            }

            totalchar = totalchar + length;

        }

        // At the end we take the siblings created in this method and override the old ones.
        return null;
    }

    public interface StringInsert {
        SplitText getText(SimpleText current, SearchUtils.StringMatch match);
    }

    /**
     * Complex method used to split up the split text in this class and replace matches to a string.
     *
     * @param matches Map containing a match and a SplitText provider
     */
    public void replaceStrings(Map<SearchUtils.StringMatch, StringInsert> matches) {
        // If there's no matches nothing should get replaced.
        if (matches.size() == 0) {
            return;
        }
        // Sort the matches and then get a nice easy iterator for navigation
        Iterator<Map.Entry<SearchUtils.StringMatch, StringInsert>> sortedMatches = new TreeMap<>(matches).entrySet().iterator();
        // List of new SimpleText to form a new SplitText.
        ArrayList<SimpleText> newSiblings = new ArrayList<>();
        // What match this is currently on.
        Map.Entry<SearchUtils.StringMatch, StringInsert> match = sortedMatches.next();

        // Total number of chars went through. Used to find where the match end and beginning is.
        int totalchar = 0;
        boolean inMatch = false;
        for (SimpleText text : getSiblings()) {
            if (text.getMessage() == null || text.getMessage().length() <= 0) {
                continue;
            }
            if (match == null) {
                // No more replacing...
                newSiblings.add(text);
                continue;
            }
            int length = text.getMessage().length();
            int last = 0;
            while (true) {
                if (length + totalchar <= match.getKey().start) {
                    newSiblings.add(text.withMessage(text.getMessage().substring(last)));
                    break;
                }
                int start = match.getKey().start - totalchar;
                int end = match.getKey().end - totalchar;
                if (inMatch) {
                    if (end <= length) {
                        inMatch = false;
                        newSiblings.add(text.withMessage(text.getMessage().substring(end)));
                        last = end;
                        if (!sortedMatches.hasNext()) {
                            match = null;
                            break;
                        }
                        match = sortedMatches.next();
                    } else {
                        break;
                    }
                } else if (start < length) {
                    // End will go onto another string
                    if (start > 0) {
                        // Add previous string section
                        newSiblings.add(text.withMessage(text.getMessage().substring(last, start)));
                    }
                    if (end >= length) {
                        newSiblings.addAll(match.getValue().getText(text, match.getKey()).getSiblings());
                        if (end == length) {
                            if (!sortedMatches.hasNext()) {
                                match = null;
                                break;
                            }
                            match = sortedMatches.next();

                        } else {
                            inMatch = true;
                        }
                        break;
                    }
                    newSiblings.addAll(match.getValue().getText(text, match.getKey()).getSiblings());
                    if (!sortedMatches.hasNext()) {
                        match = null;
                    } else {
                        match = sortedMatches.next();
                    }
                    last = end;
                    if (match == null || match.getKey().start - totalchar > length) {
                        newSiblings.add(text.withMessage(text.getMessage().substring(end)));
                        break;
                    }
                } else {
                    break;
                }
                if (match == null) {
                    break;
                }
            }
            totalchar = totalchar + length;

        }

        // At the end we take the siblings created in this method and override the old ones.
        siblings = newSiblings;

    }

    public List<SimpleText> getSiblings() {
        return siblings;
    }

    /**
     * Prefixes the time to text
     *
     * @param format Date formatter
     * @param time Current time
     */
    public void addTime(DateTimeFormatter format, LocalTime time) {
        String replaceFormat = ConfigStorage.General.TIME_TEXT_FORMAT.config.getStringValue().replaceAll("&", "§");
        ColorUtil.SimpleColor color = ConfigStorage.General.TIME_COLOR.config.getSimpleColor();
        Style style = Style.EMPTY;
        TextColor textColor = TextColor.fromRgb(color.color());
        style = style.withColor(textColor);
        SimpleText text = new SimpleText(replaceFormat.replaceAll("%TIME%", time.format(format)), style);
        siblings.add(0, text);
    }

    public void append(SimpleText text, boolean copyIfEmpty) {
        if (siblings.size() > 0) {
            SimpleText last = siblings.get(siblings.size() - 1);
            // Prevent having a ton of the same siblings in one...
            if (last.getStyle().equals(text.getStyle()) || (copyIfEmpty && text.getStyle().equals(Style.EMPTY))) {
                last.setMessage(last.getMessage() + text.getMessage());
            } else {
                siblings.add(text);
            }
        } else {
            siblings.add(text);
        }
    }

}
