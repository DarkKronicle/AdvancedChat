package io.github.darkkronicle.advancedchat.chat.formatters;

import com.mojang.brigadier.ParseResults;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.RawText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class JSONFormatter implements IMessageFormatter {


    /*
     * Released under the MIT license
     *
     * JSON Logic is from https://github.com/joeattardi/json-colorizer/blob/master/src/lib/lexer.js
     */
    public enum JSONType {
        WHITESPACE("^\\s+", ColorUtil.WHITE.withAlpha(0)),
        BRACE("^[\\{\\}]", new ColorUtil.SimpleColor(130, 130, 130,255)),
        BRACKET("^[\\[\\]]", new ColorUtil.SimpleColor(180, 180, 180, 255)),
        COLON("^:", new ColorUtil.SimpleColor(130, 130, 130, 255)),
        COMMA("^,", new ColorUtil.SimpleColor(130, 130, 130, 255)),
        NUMBER_LITERAL("^-?\\d+(?:\\.\\d+)?(?:e[+-]?\\d+)?", new ColorUtil.SimpleColor(168, 97, 199, 255)),
        STRING_KEY("^\"(?:\\\\.|[^\"\\\\])*\"(?=\\s*:)", new ColorUtil.SimpleColor(120, 156, 183, 255)),
        STRING_LITERAL("^\"(?:\\\\.|[^\"\\\\])*\"", new ColorUtil.SimpleColor(189, 215, 222, 255)),
        BOOLEAN_LITERAL("^true|^false", new ColorUtil.SimpleColor(232, 63, 113, 255)),
        NULL_LITERAL("^null", new ColorUtil.SimpleColor(194, 76, 75, 255)),
        OTHER(".", new ColorUtil.SimpleColor(210, 43, 43, 255)),
        ;

        public final String regex;
        public final ColorUtil.SimpleColor color;

        JSONType(String regex, ColorUtil.SimpleColor color) {
            this.regex = regex;
            this.color = color;
        }
    }

    @Value
    @AllArgsConstructor
    public static class JSONToken {
        SearchUtils.StringMatch match;
        JSONType type;
    }

    @Override
    public Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse) {
        String content = text.getString();
        Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(content, "\\{.+\\}", Filter.FindType.REGEX);
        if (!omatches.isPresent()) {
            return Optional.empty();
        }
        List<SearchUtils.StringMatch> matches = omatches.get();
        HashMap<SearchUtils.StringMatch, FluidText.StringInsert> replace = new HashMap<>();
        for (SearchUtils.StringMatch m : matches) {
            replace.put(m, (current, match) -> colorJson(match.match));
        }
        text.replaceStrings(replace);
        return Optional.of(text);
    }

    public FluidText colorJson(String string) {

        FluidText text = new FluidText();
        for (JSONToken token : parseJson(string)) {
            text.append(RawText.withColor(token.match.match, token.type.color));
        }
        return text;
    }

    public List<JSONToken> parseJson(String string) {
        List<JSONToken> json = new ArrayList<>();
        int index = 0;
        while (string.length() > 0) {
            for (JSONType type : JSONType.values()) {
                Optional<SearchUtils.StringMatch> omatch = SearchUtils.getMatch(string, type.regex, Filter.FindType.REGEX);
                if (!omatch.isPresent()) {
                    continue;
                }
                SearchUtils.StringMatch match = omatch.get();
                string = string.substring(match.end);
                match.end += index;
                match.start += index;
                index += match.end - match.start;
                json.add(new JSONToken(match, type));
                break;
            }

        }
        return json;
    }

}
