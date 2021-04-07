package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.util.FluidText;

import java.util.Optional;

public interface IMessageFilter {

    Optional<FluidText> filter(FluidText text);

}
