package io.github.darkkronicle.advancedchat.filters.processors;

import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;

import javax.annotation.Nullable;

public class ForwardProcessor implements IMatchProcessor {
    @Override
    public Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
        return Result.FORCE_FORWARD;
    }
}
