package io.github.darkkronicle.advancedchat.chat.formatters;

import com.mojang.brigadier.ParseResults;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import javax.annotation.Nullable;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ColorCodeFormatter implements IMessageFormatter {

    @Override
    public Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse) {
        if (parse != null) {
            return Optional.empty();
        }
        String string = text.getString();
        if (!string.contains("&")) {
            return Optional.empty();
        }
        SearchResult search = SearchResult.searchOf(string, "(?i)&[0-9A-FK-OR]", Filter.FindType.REGEX);
        if (search.size() == 0) {
            return Optional.empty();
        }
        int index = 0;
        Style last = Style.EMPTY;
        FluidText formatted = new FluidText();
        for (StringMatch match : search.getMatches()) {
            formatted.append(text.truncate(new StringMatch("", index, match.start)).fillStyle(last));
            Formatting format = Formatting.byCode(match.match.charAt(1));
            last = last.withFormatting(format);
            index = match.start;
        }
        FluidText small = text.truncate(new StringMatch("", index, string.length()));
        if (small != null && !small.getString().isEmpty()) {
            formatted.append(small.fillStyle(last));
        }
        return Optional.of(formatted);
    }

}
