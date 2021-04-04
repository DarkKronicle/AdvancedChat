package net.darkkronicle.advancedchat.chat;

import com.mojang.brigadier.ParseResults;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import net.darkkronicle.advancedchat.chat.registry.AbstractRegistry;
import net.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import net.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.RawText;
import net.darkkronicle.advancedchat.util.SearchUtils;
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
