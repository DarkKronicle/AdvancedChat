package net.darkkronicle.advancedchat.interfaces;

import net.minecraft.text.Text;

import java.util.Optional;

public interface IMessageProcessor extends IMessageFilter {

    default Optional<Text> filter(Text text) {
        process(text);
        return Optional.empty();
    }

    void process(Text text);

}
