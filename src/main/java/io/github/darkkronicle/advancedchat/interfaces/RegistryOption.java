package io.github.darkkronicle.advancedchat.interfaces;

import io.github.darkkronicle.advancedchat.chat.registry.AbstractRegistry;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Supplier;

/**
 * An interface to get a RegistryOption that can be saved/configured in game.
 * @param <TYPE> Object type to be wrapped
 */
public interface RegistryOption<TYPE> {

    /**
     * Get's the object that this option is wrapping
     * @return Object that is wrapped
     */
    TYPE getOption();

    /**
     * Whether or not this option is currently active
     * @return If it is active
     */
    boolean isActive();

    /**
     * Get's the string that will be saved inside of the JSON.
     * @return Save string
     */
    String getSaveString();

    /**
     * Copies the registry option without a parent registry
     * @return A copy of this object
     */
    default RegistryOption<TYPE> copy() {
        return copy(null);
    }

    default boolean isHidden() {
        return false;
    }

    /**
     * Copies the registry option with a parent registry that it will be tied to.
     *
     * Used for copying registries so that options can be modified easily.
     *
     * @param registry Registry that will be the parent
     * @return Copied object
     */
    RegistryOption<TYPE> copy(AbstractRegistry<TYPE, ?> registry);

    /**
     * Get's the configuration screen of this option. If this isn't null a button will be generated
     * if in a GUI.
     * @param parent Parent screen
     * @return Supplier for the config screen. Null if it doesn't exist.
     */
    default Supplier<Screen> getScreen(Screen parent) {
        if (!(getOption() instanceof IScreenSupplier)) {
            return null;
        }
        return ((IScreenSupplier) getOption()).getScreen(parent);
    }

    /**
     * Hover lines to show when in the GUI and hovering.
     * @return List of strings that contain the hover lines
     */
    default List<String> getHoverLines() {
        return null;
    }
}