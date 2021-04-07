package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;

import javax.annotation.Nullable;
import java.util.List;

public interface IMatchProcessor extends IMessageProcessor {

    @Override
    default boolean process(FluidText text, FluidText unfiltered) {
        return processMatches(text, unfiltered, null);
    }

    boolean processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable List<SearchUtils.StringMatch> matches);

    default boolean matchesOnly() {
        return true;
    }

}
