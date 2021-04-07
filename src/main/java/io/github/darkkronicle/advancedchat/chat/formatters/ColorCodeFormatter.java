package io.github.darkkronicle.advancedchat.chat.formatters;

import com.mojang.brigadier.ParseResults;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import net.minecraft.command.CommandSource;

import javax.annotation.Nullable;
import java.util.Optional;

public class ColorCodeFormatter implements IMessageFormatter {

    @Override
    public Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse) {
        return Optional.empty();
    }

}
