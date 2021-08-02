package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.util.FluidText;

import java.util.Optional;

/**
 * An interface to modify text.
 */
public interface IMessageFilter {

    /**
     * Modifies text
     * @param text Text to modify
     * @return Modified text. If empty, the text won't be changed.
     */
    Optional<FluidText> filter(FluidText text);

}
