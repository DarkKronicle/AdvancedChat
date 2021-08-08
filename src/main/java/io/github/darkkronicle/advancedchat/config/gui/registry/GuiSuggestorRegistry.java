package io.github.darkkronicle.advancedchat.config.gui.registry;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.chat.registry.ChatSuggestorRegistry;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetListRegistryOption;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetRegistryOptionEntry;
import net.minecraft.client.gui.screen.Screen;

public class GuiSuggestorRegistry extends GuiListBase<ChatSuggestorRegistry.ChatSuggestorOption, WidgetRegistryOptionEntry<ChatSuggestorRegistry.ChatSuggestorOption>, WidgetListRegistryOption<ChatSuggestorRegistry.ChatSuggestorOption>> {

    public GuiSuggestorRegistry(Screen parent) {
        super(10, 60);
        setParent(parent);
        this.title = StringUtils.translate("advancedchat.screen.suggestors");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.reCreateListWidget();
        int x = 10;
        int y = 30;
        String name = ButtonListener.Type.BACK.getDisplayName();
        int width = StringUtils.getStringWidth(name) + 10;
        ButtonGeneric generic = new ButtonGeneric(x, y, width, 20, name);
        this.addButton(generic, new ButtonListener(ButtonListener.Type.BACK, this));
        this.getListWidget().refreshEntries();
    }

    @Override
    protected WidgetListRegistryOption<ChatSuggestorRegistry.ChatSuggestorOption> createListWidget(int listX, int listY) {
        return new WidgetListRegistryOption<>(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), null, ChatSuggestorRegistry.getInstance(), this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    private void back() {
        closeGui(true);
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiSuggestorRegistry parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiSuggestorRegistry parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back"),
            ;
            private final String translation;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }

        }

    }

}