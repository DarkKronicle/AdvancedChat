package net.darkkronicle.advancedchat.interfaces;

import jdk.internal.jline.internal.Nullable;
import net.minecraft.text.Text;

import java.util.Optional;

public interface IMessageProcessor extends IMessageFilter {

    default Optional<Text> filter(Text text) {
        process(text, null);
        return Optional.empty();
    }

    boolean process(Text text, @Nullable Text unfiltered);

}
