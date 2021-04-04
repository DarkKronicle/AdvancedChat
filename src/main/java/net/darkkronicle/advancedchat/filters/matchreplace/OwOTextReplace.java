package net.darkkronicle.advancedchat.filters.matchreplace;

import maow.owo.OwO;
import maow.owo.util.ParsingUtil;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.RawText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OwOTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, List<SearchUtils.StringMatch> matches) {
        HashMap<SearchUtils.StringMatch, FluidText.StringInsert> replaceMatches = new HashMap<>();
        for (SearchUtils.StringMatch match : matches) {
            Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(match.match, "(?<!§)([A-Za-z]+)", Filter.FindType.REGEX);
            if (!omatches.isPresent()) {
                continue;
            }
            List<SearchUtils.StringMatch> foundMatches = omatches.get();
            foundMatches.forEach(stringMatch -> {
                stringMatch.start += match.start;
                stringMatch.end += match.start;
            });
            for (SearchUtils.StringMatch m : foundMatches) {
                replaceMatches.put(m, (current, match1) -> new FluidText(current.withMessage(OwO.INSTANCE.translate(match1.match))));
            }
        }
        text.replaceStrings(replaceMatches);
        text.append(new RawText(" " + ParsingUtil.parseRandomizedLetters(ParsingUtil.getRandomElement(OwO.INSTANCE.getSuffixes())), Style.EMPTY), true);
        return Optional.of(text);
    }
}
