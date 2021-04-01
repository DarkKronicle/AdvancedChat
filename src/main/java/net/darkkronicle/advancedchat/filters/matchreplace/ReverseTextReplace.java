package net.darkkronicle.advancedchat.filters.matchreplace;

import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ReverseTextReplace implements IMatchReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, @Nullable List<SearchUtils.StringMatch> matches) {
        HashMap<SearchUtils.StringMatch, SplitText.StringInsert> replaceMatches = new HashMap<>();
        for (SearchUtils.StringMatch match : matches) {
            if (match.match.length() <= 1) {
                // Can't reverse < 1
                continue;
            }
            replaceMatches.put(match, (current, match1) -> new SplitText(current.withMessage(new StringBuilder(match.match).reverse().toString())));
        }
        if (replaceMatches.size() == 0) {
            return Optional.empty();
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text.getText());
    }

}
