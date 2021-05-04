package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.StringMatch;

import javax.annotation.Nullable;
import java.util.List;

public interface IMatchProcessor extends IMessageProcessor {

    enum Result {
        FAIL(false, true, false),
        PROCESSED(true, false, false),
        FORCE_FORWARD(true, true, true),
        FORCE_STOP(true, false, true)
        ;
        public final boolean success;
        public final boolean forward;
        public final boolean force;

        Result(boolean success, boolean forward, boolean force) {
            this.success = success;
            this.forward = forward;
            this.force = force;
        }

        public static Result getFromBool(boolean success) {
            if (!success) {
                return FAIL;
            }
            return PROCESSED;
        }
    }

    @Override
    default boolean process(FluidText text, FluidText unfiltered) {
        return processMatches(text, unfiltered, null).success;
    }

    Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search);

    default boolean matchesOnly() {
        return true;
    }

}
