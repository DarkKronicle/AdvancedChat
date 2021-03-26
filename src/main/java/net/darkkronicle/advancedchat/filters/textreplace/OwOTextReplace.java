package net.darkkronicle.advancedchat.filters.textreplace;

import maow.owo.OwO;
import maow.owo.util.ParsingUtil;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OwOTextReplace implements IMatchReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchUtils.StringMatch> matches) {
        HashMap<SearchUtils.StringMatch, SplitText.StringInsert> replaceMatches = new HashMap<>();
        for (SearchUtils.StringMatch match : matches) {
            Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(match.match, "(?<!§)([A-Za-z]+)", Filter.FindType.REGEX);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }
            List<SearchUtils.StringMatch> foundMatches = omatches.get();
            foundMatches.forEach(stringMatch -> {
                stringMatch.start += match.start;
                stringMatch.end += match.start;
            });
            for (SearchUtils.StringMatch m : foundMatches) {
                replaceMatches.put(m, (current, match1) -> new SplitText(current.withMessage(OwO.INSTANCE.translate(match1.match))));
            }
        }
        text.replaceStrings(replaceMatches);
        text.append(new SimpleText(" " + ParsingUtil.parseRandomizedLetters(ParsingUtil.getRandomElement(OwO.INSTANCE.getSuffixes())), Style.EMPTY), true);
        return Optional.of(text.getText());
    }
}
