package io.github.darkkronicle.advancedchat.chat;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.RawText;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Optional;

/**
 * A class to format the chat box on the {@link io.github.darkkronicle.advancedchat.config.ConfigStorage.ChatScreen}
 */
@Environment(EnvType.CLIENT)
public class ChatFormatter {

    /**
     * The last content that was formatted
     */
    private String current = null;

    /**
     * The formatted current
     */
    private FluidText last = null;
    private final TextFieldWidget widget;
    private final ChatSuggestor suggestor;

    public ChatFormatter(TextFieldWidget widget, ChatSuggestor suggestor) {
        this.widget = widget;
        this.suggestor = suggestor;
    }

    /**
     * Format's the chat box contents
     * @param string Contents
     * @return Formatted FluidText. If nothing is changed it will be the contents with Style.EMPTY
     */
    public FluidText format(String string) {
        FluidText text = new FluidText(new RawText(string, Style.EMPTY));
        if (string.length() == 0) {
            return text;
        }
        if (suggestor.getAllSuggestions() != null) {
            HashMap<StringMatch, FluidText.StringInsert> format = new HashMap<>();
            for (AdvancedSuggestions suggestions : suggestor.getAllSuggestions()) {
                if (suggestions.getSuggestions().isEmpty()) {
                    // Don't want to format if there's nothing there...
                    continue;
                }
                StringRange range = suggestions.getRange();
                String matchString = string.subSequence(range.getStart(), range.getEnd()).toString();
                format.put(new StringMatch(matchString, range.getStart(), range.getEnd()), (current, match) -> {
                    Style style = Style.EMPTY;
                    style = style.withFormatting(Formatting.UNDERLINE);
                    TextColor textColor = TextColor.fromRgb(ConfigStorage.ChatSuggestor.AVAILABLE_SUGGESTION_COLOR.config.getSimpleColor().color());
                    style = style.withColor(textColor);
                    return new FluidText(new RawText(matchString, style));
                });
            }
            text.replaceStrings(format);
        }
        for (ChatFormatterRegistry.ChatFormatterOption option : ChatFormatterRegistry.getInstance().getAll()) {
            if (!option.isActive()) {
                continue;
            }
            Optional<FluidText> otext = option.getOption().format(text, suggestor.getParse());
            if (otext.isPresent()) {
                text = otext.get();
            }
        }
        return text;
    }

    private OrderedText set(String s, Integer integer) {
        int length = s.length();
        if (length == 0) {
            return OrderedText.EMPTY;
        }
        return last.truncate(new StringMatch(s, integer, integer + length)).asOrderedText();
    }


    public OrderedText apply(String s, Integer integer) {
        String text = widget.getText();
        if (text.equals(current)) {
            // If the content hasn't changed, use the previous one.
            return set(s, integer);
        }
        current = text;
        last = format(text);
        return set(s, integer);
    }
}
