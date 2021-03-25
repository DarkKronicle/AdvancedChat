package net.darkkronicle.advancedchat.interfaces;

import net.minecraft.text.Text;

import java.util.Optional;

public interface IMessageFilter {

    Optional<Text> filter(Text text);

}
