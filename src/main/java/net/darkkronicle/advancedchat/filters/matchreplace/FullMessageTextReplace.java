package net.darkkronicle.advancedchat.filters.matchreplace;

import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.RawText;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FullMessageTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, List<SearchUtils.StringMatch> matches) {
        StringBuilder match = new StringBuilder();
        for (SearchUtils.StringMatch m : matches) {
            match.append(m.match);
        }
        RawText base = new RawText("None", Style.EMPTY);
        ColorUtil.SimpleColor c = filter.color;
        if (c == null) {
            base = text.truncate(matches.get(0)).getRawTexts().get(0);
        } else {
            Style original = Style.EMPTY;
            TextColor textColor = TextColor.fromRgb(c.color());
            original = original.withColor(textColor);
            base = base.withStyle(original);
        }
        RawText toReplace = base.withMessage(filter.replaceTo.replaceAll("%MATCH%", match.toString()));
        return Optional.of(new FluidText(toReplace));
    }

}
