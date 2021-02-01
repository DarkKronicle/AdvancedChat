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

import net.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * @param Text text to split into different {@link SimpleText}
     */
    public SplitText(Text text) {
        text.visit((style, string) -> {
            siblings.add(new SimpleText(string, style));
            return Optional.empty();
        }, Style.EMPTY);
    }


    /**
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

    /**
     * Complex method used to split up the split text in this class and replace matches to a string.
     *
     * @param matches List of {@link net.darkkronicle.advancedchat.util.SearchText.StringMatch} to replace.
     * @param replace String to replace the matches to.
     */
    public void replaceStrings(List<SearchText.StringMatch> matches, String replace) {
        replaceStrings(matches, replace, null);
    }

    /**
     * Complex method used to split up the split text in this class and replace matches to a string.
     *
     * @param matches List of {@link net.darkkronicle.advancedchat.util.SearchText.StringMatch} to replace.
     * @param replace {@link SimpleText} to replace the matches to.
     */
    public void replaceStrings(List<SearchText.StringMatch> matches, String replace, ColorUtil.SimpleColor color) {
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
                    newSiblings.add(text.withMessage(text.getMessage().substring(togo, startedInt)));
                    if (color == null) {
                        newSiblings.add(text.withMessage(replace));
                    } else {
                        Style original = text.getStyle();
                        TextColor textColor = TextColor.fromRgb(color.color());
                        original = original.withColor(textColor);
                        newSiblings.add(text.withStyle(original).withMessage(replace));
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
                    newSiblings.add(text.withMessage(text.getMessage().substring(endedInt)));
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
                        newSiblings.add(text.withMessage(text.getMessage().substring(stopped - totalchar)));
                    } else if (stopped != 0 && stopped <= totalchar) {
                        newSiblings.add(text);
                    } else if (stopped == 0) {
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

    public static Text getTextFromText(SimpleText text) {
        TextCollector textCollector = new TextCollector();
        textCollector.add(StringVisitable.styled(text.getMessage(), text.getStyle()));
        return (Text) textCollector.getCombined();
    }

    public void addTime(DateTimeFormatter format, LocalTime time) {
        String replaceFormat = AdvancedChat.configStorage.replaceFormat.replaceAll("&", "§");
        ColorUtil.SimpleColor color = AdvancedChat.configStorage.timeColor;
        Style style = Style.EMPTY;
        TextColor textColor = TextColor.fromRgb(color.color());
        style = style.withColor(textColor);
        SimpleText text = new SimpleText(replaceFormat.replaceAll("%TIME%", time.format(format)), style);
        siblings.add(0, text);
    }

}
