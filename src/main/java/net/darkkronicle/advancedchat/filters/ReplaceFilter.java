package net.darkkronicle.advancedchat.filters;

import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ReplaceFilter extends AbstractFilter {

    private SimpleText replaceTo;
    private Filter.ReplaceType type;

    public ReplaceFilter(String filterString, SimpleText replaceTo, Filter.FindType findType, Filter.ReplaceType type) {
        super.filterString = filterString;
        this.replaceTo = replaceTo;
        this.type = type;
        super.findType = findType;
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        SplitText splitText = new SplitText(text);
        Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatch(splitText.getFullMessage(), super.filterString, findType);
        if (!omatches.isPresent()) {
            return Optional.empty();
        }
        List<SearchText.StringMatch> matches = omatches.get();
        splitText.replaceStrings(matches, replaceTo.getMessage());
        return Optional.of(splitText.getStringRenderable());
    }
}
