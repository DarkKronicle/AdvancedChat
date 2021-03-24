package net.darkkronicle.advancedchat.config.widgets;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.storage.Filter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WidgetListFilters extends WidgetListBase<Filter, WidgetFilterEntry> {

    public Filter filter;
    protected final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields = new ArrayList<>();

    @Override
    protected void reCreateListEntryWidgets() {
        this.textFields.clear();
        super.reCreateListEntryWidgets();
    }

    public WidgetListFilters(int x, int y, int width, int height, @Nullable ISelectionListener<Filter> selectionListener, Filter filter, Screen parent) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.filter = filter;
        this.setParent(parent);
    }

    public void addTextField(TextFieldWrapper<? extends GuiTextFieldGeneric> text) {
        textFields.add(text);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        clearTextFieldFocus();
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void clearTextFieldFocus() {
        for (TextFieldWrapper<? extends GuiTextFieldGeneric> field : this.textFields) {
            GuiTextFieldGeneric textField = field.getTextField();

            if (textField.isFocused()) {
                textField.setFocused(false);
                break;
            }
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        for (WidgetFilterEntry widget : this.listWidgets) {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.onKeyTyped(keyCode, scanCode, modifiers);
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
