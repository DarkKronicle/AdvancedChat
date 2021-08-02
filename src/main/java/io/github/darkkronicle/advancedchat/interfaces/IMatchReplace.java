package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An interface to replace message content from a {@link ReplaceFilter}
 *
 * Similar to {@link IMessageFilter} but supports {@link SearchResult}.
 */
public interface IMatchReplace extends IMessageFilter {

    default boolean matchesOnly() {
        return true;
    }

    /**
     * Filter text based off of previous matches.
     *
     * @param filter Filter that triggered the operation
     * @param text Text that was filtered
     * @param search Matches
     * @return Optional of new text. If returned empty the text will not be replaced
     */
    Optional<FluidText> filter(ReplaceFilter filter, FluidText text, @Nullable SearchResult search);

    @Override
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    /**
     * Whether to forward details to children as well.
     * @return Value to forward to children
     */
    default boolean useChildren() {
        return false;
    }

}
