package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.filters.ParentFilter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface IFilter extends IMessageFilter {
    @Override
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    default Optional<ColorUtil.SimpleColor> getColor() {
        return Optional.empty();
    }

    Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search);
}
