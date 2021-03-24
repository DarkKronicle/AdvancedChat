package net.darkkronicle.advancedchat.filters.TextReplace;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RainbowTextReplace implements ITextReplace {

    private final static TextColor[] COLORS = new TextColor[]{
            TextColor.fromRgb(Formatting.RED.getColorValue()),
            TextColor.fromRgb(Formatting.DARK_RED.getColorValue()),
            TextColor.fromRgb(Formatting.GOLD.getColorValue()),
            TextColor.fromRgb(Formatting.YELLOW.getColorValue()),
            TextColor.fromRgb(Formatting.GREEN.getColorValue()),
            TextColor.fromRgb(Formatting.DARK_GREEN.getColorValue()),
            TextColor.fromRgb(Formatting.DARK_BLUE.getColorValue()),
            TextColor.fromRgb(Formatting.BLUE.getColorValue()),
            TextColor.fromRgb(Formatting.LIGHT_PURPLE.getColorValue()),
            TextColor.fromRgb(Formatting.DARK_PURPLE.getColorValue())
    };
    private static int current = 0;

    public static TextColor next() {
        current++;
        if (current >= COLORS.length) {
            current = 0;
        }
        return COLORS[current];
    }

    @Override
    public Optional<Text> filter(ReplaceFilter filter, SplitText text, List<SearchText.StringMatch> matches) {
        HashMap<SearchText.StringMatch, SplitText.StringInsert> toReplace = new HashMap<>();
        for (SearchText.StringMatch m : matches) {
            Optional<List<SearchText.StringMatch>> ocharMatches = SearchText.findMatches(m.match, "(?<!§)[^§]", Filter.FindType.REGEX);
            if (!ocharMatches.isPresent()) {
                continue;
            }
            for (SearchText.StringMatch match : ocharMatches.get()) {
                toReplace.put(new SearchText.StringMatch(match.match, match.start + m.start, match.end + m.start), (current1, match1) ->
                        new SplitText(current1.withMessage(match1.match).withStyle(current1.getStyle().withColor(next()))));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text.getText());
    }
}
