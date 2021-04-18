package io.github.darkkronicle.advancedchat.chat.suggestors.gui;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import io.github.darkkronicle.advancedchat.chat.suggestors.ShortcutSuggestor;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetFilterEntry;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShortcutListWidget extends WidgetListBase<ShortcutSuggestor.Shortcut, ShortcutEntryListWidget> {

    public final ShortcutSuggestor suggestor;
    protected final List<TextFieldWrapper<GuiTextFieldGeneric>> textFields = new ArrayList<>();

    @Override
    protected void reCreateListEntryWidgets() {
        textFields.clear();
        super.reCreateListEntryWidgets();
    }

    public void addTextField(TextFieldWrapper<GuiTextFieldGeneric> text) {
        textFields.add(text);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        clearTextFieldFocus();
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void clearTextFieldFocus() {
        for (TextFieldWrapper<GuiTextFieldGeneric> field : this.textFields) {
            GuiTextFieldGeneric textField = field.getTextField();

            if (textField.isFocused()) {
                textField.setFocused(false);
                break;
            }
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        for (ShortcutEntryListWidget widget : this.listWidgets) {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    public ShortcutListWidget(int x, int y, int width, int height, @Nullable ISelectionListener<ShortcutSuggestor.Shortcut> selectionListener, ShortcutSuggestor parent, Screen screen) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.suggestor = parent;
        this.setParent(screen);
    }

    @Override
    protected ShortcutEntryListWidget createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ShortcutSuggestor.Shortcut entry) {
        return new ShortcutEntryListWidget(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }

    @Override
    protected Collection<ShortcutSuggestor.Shortcut> getAllEntries() {
        return suggestor.getShortcuts();
    }

}
