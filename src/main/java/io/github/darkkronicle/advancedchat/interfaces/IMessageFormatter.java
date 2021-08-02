package io.github.darkkronicle.advancedchat.interfaces;

import com.mojang.brigadier.ParseResults;
import io.github.darkkronicle.advancedchat.util.FluidText;
import net.minecraft.command.CommandSource;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An interface for formatting the chat text box on the chat screen.
 */
public interface IMessageFormatter {

    /**
     * Changes how the chat text bar is rendered on the chat screen
     *
     * @param text Current text that will be rendered
     * @param parse Current commands that have been parsed
     * @return Text that should render on the chat text bar. If empty it won't modify.
     */
    Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse);

}
