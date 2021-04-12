package io.github.darkkronicle.advancedchat.filters.matchreplace;

import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.RawText;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OnlyMatchTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        for (StringMatch m : search.getMatches()) {
            if (filter.color == null) {
                toReplace.put(m, (current, match) -> new FluidText(current.withMessage(search.getGroupReplacements(filter.replaceTo.replaceAll("%MATCH%", match.match), true))));
            } else {
                toReplace.put(m, (current, match) -> new FluidText(RawText.withColor(search.getGroupReplacements(filter.replaceTo.replaceAll("%MATCH%", match.match), true), filter.color)));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text);
    }

}
