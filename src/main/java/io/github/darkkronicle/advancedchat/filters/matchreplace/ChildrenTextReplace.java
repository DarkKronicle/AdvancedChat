package io.github.darkkronicle.advancedchat.filters.matchreplace;

import io.github.darkkronicle.advancedchat.filters.AbstractFilter;
import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ChildrenTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        // We don't want new filters to modify what old filters would already have done.
        // It would lead to repeats of words, and just other kinds of messes.
        // To combat this we modify all the matches that haven't been matched yet based off of the new string length.
        for (int i = 0; i < search.getMatches().size(); i++) {
            StringMatch match = search.getMatches().get(i);
            FluidText current = text.truncate(match);
            if (current == null) {
                continue;
            }
            for (AbstractFilter f : filter.getChildren()) {
                Optional<FluidText> filteredText = f.filter(current);
                if (filteredText.isPresent()) {
                    HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();

                    // Get old length and new length. As well as modify the message that is currently being modified
                    // in the match
                    int oldLength = current.getString().length();
                    current = filteredText.get();
                    int newLength = current.getString().length();
                    int modifyLength = newLength - oldLength;
                    // Take the new length and figure out how much each match needs to move to have it work.
                    for (int j = i + 1; j < search.getMatches().size(); j++) {
                        StringMatch m = search.getMatches().get(j);
                        m.start += modifyLength;
                        m.end += modifyLength;
                    }
                    // Put in the new simple text for easy use
                    final FluidText toAdd = current;
                    toReplace.put(match, (current1, match1) -> toAdd);
                    // Replace the match
                    text.replaceStrings(toReplace);
                }
            }
        }
        return Optional.of(text);
    }

    @Override
    public boolean useChildren() {
        return true;
    }
}
