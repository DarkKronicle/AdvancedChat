package net.darkkronicle.advancedchat.filters;

import lombok.NonNull;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ColorFilter extends AbstractFilter {
    private final ColorUtil.SimpleColor color;

    public ColorFilter(String filterString, Filter.FindType findType, @NonNull ColorUtil.SimpleColor color) {
        super(filterString, findType);
        this.color = color;
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        return Optional.empty();
    }

    public ColorUtil.SimpleColor getBackgroundColor(StringRenderable text) {
        SplitText splitText = new SplitText(text);
        if (SearchText.isMatch(splitText.getFullMessage(), filterString, findType)) {
            return color;
        }
        return null;
    }
}
