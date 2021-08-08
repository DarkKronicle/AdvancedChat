package io.github.darkkronicle.advancedchat.config.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.gui.registry.GuiFormatterRegistry;
import io.github.darkkronicle.advancedchat.config.gui.registry.GuiSuggestorRegistry;

import java.util.ArrayList;
import java.util.List;

public class GuiSuggesterConfig extends GuiConfigsBase {
    public static GuiConfig.ConfigGuiTab tab = GuiConfig.ConfigGuiTab.GENERAL;

    public GuiSuggesterConfig() {
        super(10, 80, AdvancedChat.MOD_ID, null, "advancedchat.screen.main");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        int rows = 1;

        for (GuiConfig.ConfigGuiTab tab : GuiConfig.ConfigGuiTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createButton(x, y, width, tab);
        }

        y += 22;
        x = width - 2;
        String name = ButtonListener.Type.CONFIG_FORMATTERS.getDisplayName();
        int w = StringUtils.getStringWidth(name) + 10;
        ButtonGeneric format = new ButtonGeneric(x - w, y, w, 20, name);
        this.addButton(format, new ButtonListener(ButtonListener.Type.CONFIG_FORMATTERS, this));
        x -= w + 2;
        name = ButtonListener.Type.CONFIG_SUGGESTORS.getDisplayName();
        w = StringUtils.getStringWidth(name) + 10;
        ButtonGeneric suggest = new ButtonGeneric(x - w, y, w, 20, name);
        this.addButton(suggest, new ButtonListener(ButtonListener.Type.CONFIG_SUGGESTORS, this));
        x -= w + 2;
        if (rows > 1) {
            int scrollbarPosition = this.getListWidget().getScrollbar().getValue();
            this.setListPosition(this.getListX(), 80 + (rows - 1) * 22);
            this.reCreateListWidget();
            this.getListWidget().getScrollbar().setValue(scrollbarPosition);
        } else {
            this.reCreateListWidget();
        }
        this.getListWidget().refreshEntries();
    }

    private int createButton(int x, int y, int width, GuiConfig.ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfig.tab != tab);
        this.addButton(button, new ButtonListenerConfigTabs(tab));

        return button.getWidth() + 2;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<ConfigStorage.SaveableConfig<? extends IConfigBase>> configs = ConfigStorage.ChatSuggestor.OPTIONS;

        ArrayList<IConfigBase> config = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> s : configs) {
            config.add(s.config);
        }

        return ConfigOptionWrapper.createFor(config);
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final GuiSuggesterConfig parent;

        public ButtonListener(Type type, GuiSuggesterConfig parent) {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (type == Type.CONFIG_FORMATTERS) {
                GuiBase.openGui(new GuiFormatterRegistry(parent));
            } else if (type == Type.CONFIG_SUGGESTORS) {
                GuiBase.openGui(new GuiSuggestorRegistry(parent));
            }
        }

        public enum Type {
            CONFIG_FORMATTERS("advancedchat.config.chatsuggestor.button.config_formatters"),
            CONFIG_SUGGESTORS("advancedchat.config.chatsuggestor.button.config_suggestors")
            ;


            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translationKey;
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }


    private static class ButtonListenerConfigTabs implements IButtonActionListener {
        private final GuiConfig.ConfigGuiTab tab;

        public ButtonListenerConfigTabs(GuiConfig.ConfigGuiTab tab) {
            this.tab = tab;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfig.tab = this.tab;
            GuiBase.openGui(new GuiConfig());
        }
    }


}
