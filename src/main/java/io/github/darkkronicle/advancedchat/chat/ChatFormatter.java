package io.github.darkkronicle.advancedchat.chat;

import com.mojang.brigadier.ParseResults;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.RawText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ChatFormatter {

    private String current = null;
    private FluidText last = null;
    private final TextFieldWidget widget;

    public ChatFormatter(TextFieldWidget widget) {
        this.widget = widget;
    }


    public FluidText format(String string, ParseResults<CommandSource> parse) {
        FluidText text = new FluidText(new RawText(string, Style.EMPTY));
        if (string.length() == 0) {
            return text;
        }
        for (ChatFormatterRegistry.ChatFormatterOption option : ChatFormatterRegistry.getInstance().getAll()) {
            Optional<FluidText> otext = option.getOption().format(text, parse);
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
        return last.truncate(new SearchUtils.StringMatch(s, integer, integer + length)).asOrderedText();
    }


    public OrderedText apply(String s, Integer integer, ParseResults<CommandSource> parse) {
        String text = widget.getText();
        if (text.equals(current)) {
            return set(s, integer);
        }
        current = text;
        last = format(text, parse);
        return set(s, integer);
    }
}
