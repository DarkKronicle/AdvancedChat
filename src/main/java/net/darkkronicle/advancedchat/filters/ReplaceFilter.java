package net.darkkronicle.advancedchat.filters;

import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.List;
import java.util.Optional;

/**
 * <h1>ReplaceFilter</h1>
 * Filter used for replacing matches in a StringRenderable
 */
@Environment(EnvType.CLIENT)
public class ReplaceFilter extends AbstractFilter {

    private String replaceTo;
    private Filter.ReplaceType type;
    private ColorUtil.SimpleColor color;

    public ReplaceFilter(String filterString, String replaceTo, Filter.FindType findType, Filter.ReplaceType type, ColorUtil.SimpleColor color) {
        super.filterString = filterString;
        this.replaceTo = replaceTo;
        this.type = type;
        this.color = color;
        super.findType = findType;
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        // Grabs SplitText for easy mutability.
        SplitText splitText = new SplitText(text);
        if (type == Filter.ReplaceType.ONLYMATCH) {
            Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatches(splitText.getFullMessage(), super.filterString, findType);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }
            List<SearchText.StringMatch> matches = omatches.get();
            splitText.replaceStrings(matches, replaceTo, color);
            return Optional.of(splitText.getStringRenderable());

        } else if (type == Filter.ReplaceType.FULLMESSAGE) {
            if (SearchText.isMatch(splitText.getFullMessage(), super.filterString, findType)) {
                SimpleText toReplace = new SimpleText(replaceTo, Style.EMPTY);
                if (color != null) {
                    Style original = Style.EMPTY;
                    TextColor textColor = TextColor.fromRgb(color.color());
                    original = original.withColor(textColor);
                    toReplace = toReplace.withStyle(original);
                }
                return Optional.of(SplitText.getStringRenderableFromText(toReplace));

            }
        }
        return Optional.empty();
    }
}
