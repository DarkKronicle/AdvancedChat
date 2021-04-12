package io.github.darkkronicle.advancedchat.filters;

import io.github.darkkronicle.advancedchat.config.Filter;
import lombok.NonNull;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

/**
 * Filter used to change the background color of a message.
 */
@Environment(EnvType.CLIENT)
public class ColorFilter extends AbstractFilter {
    /**
     * {@link ColorUtil.SimpleColor} that will change the background color.
     */
    private final ColorUtil.SimpleColor color;

    public ColorFilter(String filterString, Filter.FindType findType, @NonNull ColorUtil.SimpleColor color) {
        super(filterString, findType);
        this.color = color;
    }

    // Doesn't change anything in text. Only happens after it goes through other filters.
    @Override
    public Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    // if returned null it won't do anything, but if not null then it will have the default color.
    // Probably not perfect to use null, may come back later.
    public ColorUtil.SimpleColor getBackgroundColor(FluidText text) {
        if (SearchUtils.isMatch(text.getString(), filterString, findType)) {
            return color;
        }
        return null;
    }
}