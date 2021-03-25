package net.darkkronicle.advancedchat.filters.TextReplace;

import net.darkkronicle.advancedchat.filters.AbstractFilter;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ChildrenTextReplace implements IMatchReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchUtils.StringMatch> matches) {
        // We don't want new filters to modify what old filters would already have done.
        // It would lead to repeats of words, and just other kinds of messes.
        // To combat this we modify all the matches that haven't been matched yet based off of the new string length.
        for (int i = 0; i < matches.size(); i++) {
            SearchUtils.StringMatch match = matches.get(i);
            SplitText current = text.truncate(match);
            if (current == null) {
                continue;
            }
            for (AbstractFilter f : filter.getChildren()) {
                Optional<Text> filteredtext = f.filter(current.getText());
                if (filteredtext.isPresent()) {
                    HashMap<SearchUtils.StringMatch, SplitText.StringInsert> toReplace = new HashMap<>();

                    // Get old length and new length. As well as modify the message that is currently being modified
                    // in the match
                    int oldLength = current.getFullMessage().length();
                    current = new SplitText(filteredtext.get());
                    int newLength = current.getFullMessage().length();
                    int modifyLength = newLength - oldLength;
                    // Take the new length and figure out how much each match needs to move to have it work.
                    for (int j = i + 1; j < matches.size(); j++) {
                        SearchUtils.StringMatch m = matches.get(j);
                        m.start += modifyLength;
                        m.end += modifyLength;
                    }
                    // Put in the new simple text for easy use
                    final SplitText toAdd = current;
                    toReplace.put(match, (current1, match1) -> toAdd);
                    // Replace the match
                    text.replaceStrings(toReplace);
                }
            }
        }
        return Optional.of(text.getText());
    }

}
