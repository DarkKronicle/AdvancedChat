package io.github.darkkronicle.advancedchat.config.gui.registry;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetListRegistryOption;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetRegistryOptionEntry;
import net.minecraft.client.gui.screen.Screen;


public class GuiFormatterRegistry extends GuiListBase<ChatFormatterRegistry.ChatFormatterOption, WidgetRegistryOptionEntry<ChatFormatterRegistry.ChatFormatterOption>, WidgetListRegistryOption<ChatFormatterRegistry.ChatFormatterOption>> {

    public GuiFormatterRegistry(Screen parent) {
        super(10, 60);
        setParent(parent);
        this.title = StringUtils.translate("advancedchat.screen.formatters");
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
    }

    @Override
    protected WidgetListRegistryOption<ChatFormatterRegistry.ChatFormatterOption> createListWidget(int listX, int listY) {
        return new WidgetListRegistryOption<>(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), null, ChatFormatterRegistry.getInstance(), this);
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
        GuiBase.openGui(getParent());
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiFormatterRegistry parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiFormatterRegistry parent) {
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
