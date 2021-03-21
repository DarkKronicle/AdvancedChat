package net.darkkronicle.advancedchat.config.widgets;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.storage.Filter;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.Collection;

public class WidgetListFilters extends WidgetListBase<Filter, WidgetFilterEntry> {

    public Filter filter;

    public WidgetListFilters(int x, int y, int width, int height, @Nullable ISelectionListener<Filter> selectionListener, Filter filter, Screen parent) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.filter = filter;
        this.setParent(parent);
    }

    @Override
    protected WidgetFilterEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, Filter entry) {
        return new WidgetFilterEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }

    @Override
    protected Collection<Filter> getAllEntries() {
        if (filter != null) {
            return filter.getChildren();
        }
        return ConfigStorage.FILTERS;
    }
}
