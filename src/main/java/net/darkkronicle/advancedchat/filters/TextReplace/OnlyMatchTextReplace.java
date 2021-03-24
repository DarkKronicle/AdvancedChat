package net.darkkronicle.advancedchat.filters.TextReplace;

import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.ITextReplace;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OnlyMatchTextReplace implements ITextReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchText.StringMatch> matches) {
        HashMap<SearchText.StringMatch, SplitText.StringInsert> toReplace = new HashMap<>();
        for (SearchText.StringMatch m : matches) {
            if (filter.color == null) {
                toReplace.put(m, (current, match) -> new SplitText(current.withMessage(filter.replaceTo.replaceAll("%MATCH%", match.match))));
            } else {
                toReplace.put(m, (current, match) -> new SplitText(SimpleText.withColor(filter.replaceTo.replaceAll("%MATCH%", match.match), filter.color)));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text.getText());
    }

}
