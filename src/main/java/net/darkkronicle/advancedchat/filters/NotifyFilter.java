package net.darkkronicle.advancedchat.filters;

import net.minecraft.text.StringRenderable;

import java.util.Optional;

public class NotifyFilter extends AbstractFilter {
    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        return Optional.empty();
    }
}
