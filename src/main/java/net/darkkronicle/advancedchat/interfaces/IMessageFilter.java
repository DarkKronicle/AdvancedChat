package net.darkkronicle.advancedchat.interfaces;

import net.darkkronicle.advancedchat.util.FluidText;

import java.util.Optional;

public interface IMessageFilter {

    Optional<FluidText> filter(FluidText text);

}
