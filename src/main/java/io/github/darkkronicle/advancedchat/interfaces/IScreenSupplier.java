package io.github.darkkronicle.advancedchat.interfaces;

import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IScreenSupplier {

    Supplier<Screen> getScreen(@Nullable Screen parent);

}
