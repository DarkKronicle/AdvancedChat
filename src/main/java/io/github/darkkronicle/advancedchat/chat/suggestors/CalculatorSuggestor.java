package io.github.darkkronicle.advancedchat.chat.suggestors;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CalculatorSuggestor implements IMessageSuggestor {

    private static final String BRACKET_REGEX = "\\[[^\\[\\]]*\\]";
    public static final String NAN = "NaN";

    @Override
    public Optional<List<Suggestions>> suggest(String text) {
        if (!text.contains("[") || !text.contains("]")) {
            return Optional.empty();
        }
        List<StringMatch> matches = SearchUtils.findMatches(text, BRACKET_REGEX, Filter.FindType.REGEX).orElse(null);
        if (matches == null) {
            return Optional.empty();
        }
        int last = -1;
        ArrayList<Suggestions> suggest = new ArrayList<>();
        for (StringMatch m : matches) {
            if (m.start < last || m.end - m.start < 1) {
                // Don't want overlapping matches (just in case) or too small
                continue;
            }
            last = m.end;
            String string = m.match.substring(1, m.match.length() - 1);
            Expression expression = new Expression(string);
            double val = expression.calculate();
            String message = NAN;
            if (!Double.isNaN(val)) {
                message = String.valueOf(val);
            }
            StringRange range = new StringRange(m.start, m.end);
            suggest.add(new Suggestions(range, new ArrayList<>(Collections.singleton(new Suggestion(range, message)))));
        }
        if (suggest.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(suggest);
    }
}
