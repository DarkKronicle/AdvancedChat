package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.util.FluidText;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IMessageProcessor extends IMessageFilter {

    default Optional<FluidText> filter(FluidText text) {
        process(text, null);
        return Optional.empty();
    }

    boolean process(FluidText text, @Nullable FluidText unfiltered);

}
