package net.darkkronicle.advancedchat.filters.TextReplace;

import maow.owo.OwO;
import maow.owo.util.ParsingUtil;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.ITextReplace;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
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
public class OwOTextReplace implements ITextReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchText.StringMatch> matches) {
        HashMap<SearchText.StringMatch, SplitText.StringInsert> replaceMatches = new HashMap<>();
        for (SearchText.StringMatch match : matches) {
            Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatches(match.match, "(?<!§)([A-Za-z]+)", Filter.FindType.REGEX);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }
            List<SearchText.StringMatch> foundMatches = omatches.get();
            foundMatches.forEach(stringMatch -> {
                stringMatch.start += match.start;
                stringMatch.end += match.start;
            });
            for (SearchText.StringMatch m : foundMatches) {
                replaceMatches.put(m, (current, match1) -> new SplitText(current.withMessage(OwO.INSTANCE.translate(match1.match))));
            }
        }
        text.replaceStrings(replaceMatches);
        text.append(new SimpleText(" " + ParsingUtil.parseRandomizedLetters(ParsingUtil.getRandomElement(OwO.INSTANCE.getSuffixes())), Style.EMPTY));
        return Optional.of(text.getText());
    }
}
