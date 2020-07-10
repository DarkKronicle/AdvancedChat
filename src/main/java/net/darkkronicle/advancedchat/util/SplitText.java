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

@Environment(EnvType.CLIENT)
public class SplitText {

    private ArrayList<SimpleText> siblings = new ArrayList<>();

    public SplitText(StringRenderable stringRenderable) {
        stringRenderable.visit((style, string) -> {
            siblings.add(new SimpleText(string, style));
            return Optional.empty();
        }, Style.EMPTY);
    }

    // Defensive copying of Style
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

    public StringRenderable getStringRenderable() {
        TextCollector textCollector = new TextCollector();
        for (SimpleText text : getSiblings()) {
            textCollector.add(StringRenderable.styled(text.getMessage(), text.getStyle()));
        }
        return textCollector.getCombined();
    }

    public void replaceStrings(List<SearchText.StringMatch> matches, String replace) {
        ArrayList<SimpleText> newSiblings = new ArrayList<>();
        int stopped = 0;
        int matchnum = 0;
        for (SearchText.StringMatch match : matches) {
            matchnum++;
            SimpleText startedText = null;
            int startedInt;
            SimpleText endedText = null;
            int endedInt;
            int totalchar = 0;
            boolean ended = false;
            boolean started = false;
            for (SimpleText text : getSiblings()) {
                if (text.getMessage() == null || text.getMessage().length() <= 0) {
                    continue;
                }

                int length = text.getMessage().length();
                boolean modified = false;


                if (totalchar + length > match.start && !started) {
                    startedText = text;
                    startedInt = match.start - totalchar;
                    int togo = 0;
                    if (stopped != 0 && totalchar < stopped && totalchar + length > stopped) {
                        togo = stopped - totalchar;
                    }
                    newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(togo, startedInt)));
                    newSiblings.add(text.copySimpleText().setMessage(replace));
                    started = true;
                    modified = true;
                }

                if (length + totalchar >= match.end && !ended) {
                    endedText = text;
                    endedInt = match.end - totalchar;
                    if (matches.size() > matchnum) {
                        stopped = match.end;
                        continue;
                    }
                    newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(endedInt)));
                    ended = true;
                    modified = true;

                }
                if (started && !ended) {
                    modified = true;
                }


                if (!modified) {
                    if (stopped != 0 && totalchar < stopped && totalchar + length > stopped) {
                        newSiblings.add(text.copySimpleText().setMessage(text.getMessage().substring(stopped - totalchar)));
                    } else if (stopped != 0 && stopped <= totalchar) {
                        newSiblings.add(text);
                    }
                }

                totalchar = totalchar + length;

            }

            if (startedText == null || endedText == null) {
                System.out.println("Something went wrong!");
                return;
            }

        }
        siblings = newSiblings;

    }

    public List<SimpleText> getSiblings() {
        return siblings;
    }

}
