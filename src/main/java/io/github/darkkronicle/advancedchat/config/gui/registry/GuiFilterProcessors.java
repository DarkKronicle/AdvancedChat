package io.github.darkkronicle.advancedchat.config.gui.registry;

import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.config.gui.GuiFilterEditor;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetListRegistryOption;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetRegistryOptionEntry;


public class GuiFilterProcessors extends GuiListBase<MatchProcessorRegistry.MatchProcessorOption, WidgetRegistryOptionEntry<MatchProcessorRegistry.MatchProcessorOption>, WidgetListRegistryOption<MatchProcessorRegistry.MatchProcessorOption>> {

    private final GuiFilterEditor parent;

    public GuiFilterProcessors(GuiFilterEditor parent) {
        super(10, 60);
        this.parent = parent;
        this.setParent(parent.getParent());
        this.title = parent.filter.getName().config.getStringValue();

    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;

        int rows = 1;

        for (GuiFilterEditor.FilterTab tab : GuiFilterEditor.FilterTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createTabButton(x, y, width, tab);
        }

        this.setListPosition(this.getListX(), 68 + (rows - 1) * 22);
        this.reCreateListWidget();

        y += 24;
        x = 10;

        this.addButton(x, y, ButtonListener.Type.BACK, false);
    }

    protected int addButton(int x, int y, ButtonListener.Type type, boolean rightAlign) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, rightAlign, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth();
    }

    private int createTabButton(int x, int y, int width, GuiFilterEditor.FilterTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.parent.tab != tab);
        this.addButton(button, new GuiFilterEditor.ButtonListenerFilterTabs(tab, this.parent));

        return button.getWidth() + 2;
    }

    public void back() {
        closeGui(true);
    }

    @Override
    protected WidgetListRegistryOption<MatchProcessorRegistry.MatchProcessorOption> createListWidget(int listX, int listY) {
        return new WidgetListRegistryOption<>(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), null, parent.filter.getProcessors(), this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    private static class ButtonListener implements IButtonActionListener {

        private final ButtonListener.Type type;
        private final GuiFilterProcessors gui;

        public ButtonListener(ButtonListener.Type type, GuiFilterProcessors gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                this.gui.back();
            }
        }

        public enum Type {
            BACK("back")
            ;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translate(translationKey);
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }

}
