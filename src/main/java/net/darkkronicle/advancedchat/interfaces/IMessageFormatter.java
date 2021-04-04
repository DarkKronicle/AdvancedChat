package net.darkkronicle.advancedchat.interfaces;

import com.mojang.brigadier.ParseResults;
import net.darkkronicle.advancedchat.util.FluidText;
import net.minecraft.command.CommandSource;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IMessageFormatter {

    Optional<FluidText> format(FluidText text, @Nullable ParseResults<CommandSource> parse);

}
