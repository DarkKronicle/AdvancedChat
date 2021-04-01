package net.darkkronicle.advancedchat.filters.matchreplace;

import net.darkkronicle.advancedchat.config.Filter;
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
public class RomanNumeralTextReplace implements IMatchReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchUtils.StringMatch> matches) {
        HashMap<SearchUtils.StringMatch, SplitText.StringInsert> replaceMatches = new HashMap<>();
        for (SearchUtils.StringMatch match : matches) {
            Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(match.match, "[0-9]+", Filter.FindType.REGEX);
            if (!omatches.isPresent()) {
                continue;
            }
            List<SearchUtils.StringMatch> foundMatches = omatches.get();
            foundMatches.forEach(stringMatch -> {
                stringMatch.start += match.start;
                stringMatch.end += match.start;
            });
            for (SearchUtils.StringMatch m : foundMatches) {
                try {
                    replaceMatches.put(m, (current, match1) -> new SplitText(current.withMessage(SearchUtils.toRoman(Integer.parseInt(m.match)))));
                } catch (Exception e) {
                    // Not an integer
                }
            }
        }
        if (replaceMatches.size() == 0) {
            return Optional.empty();
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text.getText());
    }

}
