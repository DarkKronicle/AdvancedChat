package io.github.darkkronicle.advancedchat.chat.formatters;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.RawText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Style;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CommandColorer implements IMessageFormatter {

    private static final ColorUtil.SimpleColor INFO = new ColorUtil.SimpleColor(180, 180, 180, 255);

    private static final ColorUtil.SimpleColor[] COLORS = new ColorUtil.SimpleColor[]{
            new ColorUtil.SimpleColor(160, 172, 219, 255),
            new ColorUtil.SimpleColor(156, 214, 162, 255),
            new ColorUtil.SimpleColor(129, 110, 224, 255)
    };

    @Override
    public Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse) {
        if (parse == null) {
            return Optional.empty();
        }
        CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();
        HashMap<StringMatch, FluidText.StringInsert> replace = new HashMap<>();
        int color = -1;
        int lowest = -1;
        for (ParsedArgument<CommandSource, ?> commandSourceParsedArgument : commandContextBuilder.getArguments().values()) {
            int start = commandSourceParsedArgument.getRange().getStart();
            int end = commandSourceParsedArgument.getRange().getEnd();
            StringMatch match = new StringMatch(text.getString().subSequence(start, end).toString(), start, end);
            if (lowest == -1 || start < lowest) {
                lowest = start;
            }
            color++;
            if (color >= COLORS.length) {
                color = 0;
            }
            final int thisCol = color;
            replace.put(match, (current, match1) -> {
                if (current.getStyle().equals(Style.EMPTY)) {
                    return new FluidText(RawText.withColor(match1.match, COLORS[thisCol]));
                }
                return new FluidText(new RawText(match1.match, current.getStyle()));
            });

        }
        if (lowest == -1) {
            lowest = text.getString().length();
        }
        replace.put(new StringMatch(text.getString().substring(0, lowest), 0, lowest), (current, match) -> {
            if (current.getStyle().equals(Style.EMPTY)) {
                return new FluidText(RawText.withColor(match.match, INFO));
            }
            return new FluidText(new RawText(match.match, current.getStyle()));
        });
        text.replaceStrings(replace);
        return Optional.of(text);
    }


}
