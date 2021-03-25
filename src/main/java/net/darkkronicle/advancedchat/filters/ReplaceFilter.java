package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Filter used for replacing matches in a Text
 */
@Environment(EnvType.CLIENT)
public class ReplaceFilter extends AbstractFilter {

    public final String replaceTo;
    public final Filter.ReplaceType type;
    public final ColorUtil.SimpleColor color;

    @Getter
    private ArrayList<AbstractFilter> children = new ArrayList<>();

    public void addChild(AbstractFilter filter) {
        children.add(filter);
    }

    public ReplaceFilter(String filterString, String replaceTo, Filter.FindType findType, Filter.ReplaceType type, ColorUtil.SimpleColor color) {
        super.filterString = filterString;
        this.replaceTo = replaceTo;
        this.type = type;
        this.color = color;
        super.findType = findType;
    }

    @Override
    public Optional<Text> filter(Text text) {
        // Grabs SplitText for easy mutability.
        if (type.textReplace == null) {
            return Optional.empty();
        }
        SplitText splitText = new SplitText(text);
        IMatchReplace replace = type.textReplace;
        if (replace.matchesOnly()) {
            Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(splitText.getFullMessage(), super.filterString, findType);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }
            return replace.filter(this, splitText, omatches.get());
        } else {
            return replace.filter(this, splitText, null);
        }
    }
}
