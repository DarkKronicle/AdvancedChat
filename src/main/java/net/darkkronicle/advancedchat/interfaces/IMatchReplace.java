package net.darkkronicle.advancedchat.interfaces;

import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.SearchUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IMatchReplace extends IMessageFilter {

    default boolean matchesOnly() {
        return true;
    }

    Optional<FluidText> filter(ReplaceFilter filter, FluidText text, @Nullable List<SearchUtils.StringMatch> matches);

    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    default boolean useChildren() {
        return false;
    }

}
