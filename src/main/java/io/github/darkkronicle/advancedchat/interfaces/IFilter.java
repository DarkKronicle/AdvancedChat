package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.filters.ParentFilter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * An interface for chat filters. Filters can change text content or background color.
 */
public interface IFilter extends IMessageFilter {

    /**
     * For {@link IFilter} this is disabled by default.
     *
     * @param text Text to modify
     * @return Modified text
     */
    @Override
    @Deprecated
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    /**
     * Get's the color that the background should be.
     * @return SimpleColor that the background should be. If empty it won't change the color
     */
    default Optional<ColorUtil.SimpleColor> getColor() {
        return Optional.empty();
    }

    /**
     * Filters text that is
     *
     * @param filter Filter that the text is being filtered from
     * @param text Current text that will be filtered
     * @param unfiltered The unfiltered version of the text
     * @param search Match results
     * @return Modified text. If empty it won't modify the current text.
     */
    Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search);
}
