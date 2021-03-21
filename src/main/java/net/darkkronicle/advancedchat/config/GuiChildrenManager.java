package net.darkkronicle.advancedchat.config;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.darkkronicle.advancedchat.config.widgets.WidgetFilterEntry;
import net.darkkronicle.advancedchat.config.widgets.WidgetListFilters;
import net.darkkronicle.advancedchat.storage.Filter;

import javax.annotation.Nullable;

public class GuiChildrenManager extends GuiListBase<Filter, WidgetFilterEntry, WidgetListFilters> implements ISelectionListener<Filter> {

    private final GuiFilterEditor parent;

    protected GuiChildrenManager(GuiFilterEditor parent) {
        super(10, 60);
        this.title = parent.filter.getName().config.getStringValue();
        this.parent = parent;
        if (parent.getParent() != null) {
            this.setParent(parent.getParent());
        } else {
            this.setParent(parent);
        }
    }

    @Override
    protected WidgetListFilters createListWidget(int listX, int listY) {
        return new WidgetListFilters(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, parent.filter, this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
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
        this.addButton(this.width - 10, y, ButtonListener.Type.ADD_FILTER, true);
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

    private static class ButtonListener implements IButtonActionListener {

        private final ButtonListener.Type type;
        private final GuiChildrenManager gui;

        public ButtonListener(ButtonListener.Type type, GuiChildrenManager gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.ADD_FILTER) {
                this.gui.parent.filter.getChildren().add(new Filter());
                this.gui.getListWidget().refreshEntries();
            } else if (this.type == Type.BACK) {
                this.gui.back();
            }
        }

        public enum Type {
            ADD_FILTER("addfilter"),
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

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    @Override
    public void onSelectionChange(@Nullable Filter entry) {

    }
}
