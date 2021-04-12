package io.github.darkkronicle.advancedchat.filters.matchreplace;

import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ReverseTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> replaceMatches = new HashMap<>();
        for (StringMatch match : search.getMatches()) {
            if (match.match.length() <= 1) {
                // Can't reverse < 1
                continue;
            }
            replaceMatches.put(match, (current, match1) -> new FluidText(current.withMessage(new StringBuilder(match.match).reverse().toString())));
        }
        if (replaceMatches.size() == 0) {
            return Optional.empty();
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text);
    }

}
