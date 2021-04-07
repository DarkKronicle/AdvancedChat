package io.github.darkkronicle.advancedchat.filters.matchreplace;

import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.RawText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OnlyMatchTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, List<SearchUtils.StringMatch> matches) {
        HashMap<SearchUtils.StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        for (SearchUtils.StringMatch m : matches) {
            if (filter.color == null) {
                toReplace.put(m, (current, match) -> new FluidText(current.withMessage(filter.replaceTo.replaceAll("%MATCH%", match.match))));
            } else {
                toReplace.put(m, (current, match) -> new FluidText(RawText.withColor(filter.replaceTo.replaceAll("%MATCH%", match.match), filter.color)));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text);
    }

}
