package io.github.darkkronicle.advancedchat.chat.suggestors.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.chat.suggestors.ShortcutSuggestor;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;


@Environment(EnvType.CLIENT)
public class ShortcutEntryListWidget extends WidgetListEntryBase<ShortcutSuggestor.Shortcut> {

    private final ShortcutListWidget parent;
    private final boolean isOdd;
    private TextFieldWrapper<GuiTextFieldGeneric> name;
    private TextFieldWrapper<GuiTextFieldGeneric> replace;

    public String getReplace() {
        return replace.getTextField().getText();
    }

    public String getName() {
        return name.getTextField().getText();
    }

    public ShortcutEntryListWidget(int x, int y, int width, int height, boolean isOdd, ShortcutSuggestor.Shortcut entry, int listIndex, ShortcutListWidget parent) {
        super(x, y, width, height, entry, listIndex);
        this.isOdd = isOdd;
        this.parent = parent;
        y += 1;
        int pos = x + width - 2;

        int removeWidth = addButton(pos, y, ButtonListener.Type.REMOVE) + 1;
        int nameWidth = 100;
        pos -= removeWidth;
        int replaceWidth = width - removeWidth - nameWidth + 1;
        GuiTextFieldGeneric replaceField = new GuiTextFieldGeneric(pos - replaceWidth, y, replaceWidth, 20, MinecraftClient.getInstance().textRenderer);
        replaceField.setMaxLength(512);
        replaceField.setText(entry.getReplace());
        replace = new TextFieldWrapper<>(replaceField, new SaveListener(this, false));
        parent.addTextField(replace);

        pos -= replaceWidth + 1;
        GuiTextFieldGeneric nameField = new GuiTextFieldGeneric(pos - nameWidth, y, nameWidth, 20, MinecraftClient.getInstance().textRenderer);
        nameField.setMaxLength(512);
        nameField.setText(entry.getName());
        name = new TextFieldWrapper<>(nameField, new SaveListener(this, true));
        parent.addTextField(name);


    }

    private static class SaveListener implements ITextFieldListener<GuiTextFieldGeneric> {

        private final ShortcutEntryListWidget parent;
        private final boolean name;

        public SaveListener(ShortcutEntryListWidget parent, boolean name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField) {
            if (name) {
                parent.entry.setName(textField.getText());
            } else {
                parent.entry.setReplace(textField.getText());
            }
            return false;
        }

    }

    protected int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth() + 1;
    }

    @Override
    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers) {
        if (this.name.isFocused()) {
            return this.name.onKeyTyped(keyCode, scanCode, modifiers);
        } else if (this.replace.isFocused()) {
            return this.replace.onKeyTyped(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    protected boolean onCharTypedImpl(char charIn, int modifiers) {
        if (this.name.onCharTyped(charIn, modifiers)) {
            return true;
        } else if (this.replace.onCharTyped(charIn, modifiers)) {
            return true;
        }

        return super.onCharTypedImpl(charIn, modifiers);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton)) {
            return true;
        }

        boolean ret = false;

        if (this.name != null) {
            ret = this.name.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (this.replace != null && !ret) {
            ret = this.replace.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (!this.subWidgets.isEmpty()) {
            for (WidgetBase widget : this.subWidgets) {
                ret |= widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        return ret;
    }

    protected void drawTextFields(int mouseX, int mouseY, MatrixStack matrixStack) {
        if (this.name != null) {
            this.name.getTextField().render(matrixStack, mouseX, mouseY, 0f);
        }
        if (this.replace != null) {
            this.replace.getTextField().render(matrixStack, mouseX, mouseY, 0f);
        }
    }

    private static class ButtonListener implements IButtonActionListener {

        private final ButtonListener.Type type;
        private final ShortcutEntryListWidget parent;


        public ButtonListener(ButtonListener.Type type, ShortcutEntryListWidget parent) {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (type == ButtonListener.Type.REMOVE) {
                parent.parent.suggestor.removeShortcut(parent.entry);
                parent.parent.refreshEntries();
                AdvancedChat.chatTab.setUpTabs();
            }
        }

        public enum Type {
            REMOVE("remove"),
            ;

            private final String translate;

            Type(String name) {
                this.translate = translate(name);
            }

            private static String translate(String key) {
                return "advancedchat.config.shortcutmenu." + key;
            }

            public String getDisplayName() {
                return StringUtils.translate(translate);
            }

        }

    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(150).color());
        } else if (this.isOdd) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(70).color());
        } else {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(50).color());
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        this.drawTextFields(mouseX, mouseY, matrixStack);

        super.render(mouseX, mouseY, selected, matrixStack);

        RenderUtils.disableDiffuseLighting();
    }
}
