package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.chat.registry.AbstractRegistry;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Supplier;

public interface RegistryOption<TYPE> {
    TYPE getOption();

    boolean isActive();

    String getSaveString();

    default RegistryOption<TYPE> copy() {
        return copy(null);
    }

    RegistryOption<TYPE> copy(AbstractRegistry<TYPE, ?> registry);

    default Supplier<Screen> getScreen(Screen parent) {
        if (!(getOption() instanceof IScreenSupplier)) {
            return null;
        }
        return ((IScreenSupplier) getOption()).getScreen(parent);
    }

    default List<String> getHoverLines() {
        return null;
    }
}