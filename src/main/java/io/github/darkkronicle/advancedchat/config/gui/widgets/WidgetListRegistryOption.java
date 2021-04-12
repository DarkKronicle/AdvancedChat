package io.github.darkkronicle.advancedchat.config.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import io.github.darkkronicle.advancedchat.chat.registry.AbstractRegistry;
import io.github.darkkronicle.advancedchat.interfaces.ConfigRegistryOption;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.Collection;

public class WidgetListRegistryOption<T extends ConfigRegistryOption<?>> extends WidgetListBase<T, WidgetRegistryOptionEntry<T>> {

    private final AbstractRegistry<?, T>  registry;

    public WidgetListRegistryOption(int x, int y, int width, int height, @Nullable ISelectionListener<T> selectionListener, AbstractRegistry<?, T> registry, Screen parent) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.setParent(parent);
        this.registry = registry;
    }


    @Override
    protected WidgetRegistryOptionEntry<T> createListEntryWidget(int x, int y, int listIndex, boolean isOdd, T entry) {
        return new WidgetRegistryOptionEntry<T>(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }

    @Override
    protected Collection<T> getAllEntries() {
        return registry.getAll();
    }
}
