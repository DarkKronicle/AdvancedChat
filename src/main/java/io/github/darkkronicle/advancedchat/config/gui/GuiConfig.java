package io.github.darkkronicle.advancedchat.config.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.AdvancedChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Based off of https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/GuiConfigs.java
// Released under GNU LGPL
public class GuiConfig extends GuiConfigsBase {

    public static ConfigGuiTab tab = ConfigGuiTab.GENERAL;

    public GuiConfig() {
        super(10, 50, AdvancedChat.MOD_ID, null, "advancedchat.screen.main");
    }

    @Override
    public void initGui() {
        if (GuiConfig.tab == ConfigGuiTab.FILTERS) {
            GuiBase.openGui(new GuiFilterManager());
            return;
        }
        if (GuiConfig.tab == ConfigGuiTab.TABS) {
            GuiBase.openGui(new GuiTabManager());
            return;
        }
        if (GuiConfig.tab == ConfigGuiTab.CHAT_SUGGESTOR) {
            GuiBase.openGui(new GuiSuggesterConfig());
            return;
        }

        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        int rows = 1;

        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createButton(x, y, width, tab);
        }

        if (rows > 1) {
            int scrollbarPosition = this.getListWidget().getScrollbar().getValue();
            this.setListPosition(this.getListX(), 50 + (rows - 1) * 22);
            this.reCreateListWidget();
            this.getListWidget().getScrollbar().setValue(scrollbarPosition);
            this.getListWidget().refreshEntries();
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfig.tab != tab);
        this.addButton(button, new ButtonListenerConfigTabs(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<ConfigStorage.SaveableConfig<? extends IConfigBase>> configs;
        ConfigGuiTab tab = GuiConfig.tab;

        if (tab == ConfigGuiTab.GENERAL) {
            configs = ConfigStorage.General.OPTIONS;
        } else if (tab == ConfigGuiTab.CHAT_SCREEN) {
            configs = ConfigStorage.ChatScreen.OPTIONS;
        } else if (tab == ConfigGuiTab.CHAT_LOG) {
            configs = ConfigStorage.ChatLog.OPTIONS;
        } else if (tab == ConfigGuiTab.CHAT_SUGGESTOR) {
            configs = ConfigStorage.ChatSuggestor.OPTIONS;
        } else {
            return Collections.emptyList();
        }
        ArrayList<IConfigBase> config = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> s : configs) {
            config.add(s.config);
        }

        return ConfigOptionWrapper.createFor(config);

    }


    private static class ButtonListenerConfigTabs implements IButtonActionListener {
        private final GuiConfig parent;
        private final ConfigGuiTab tab;

        public ButtonListenerConfigTabs(ConfigGuiTab tab, GuiConfig parent) {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfig.tab = this.tab;

            if (this.tab == ConfigGuiTab.FILTERS) {
                GuiBase.openGui(new GuiFilterManager());
            } else if (this.tab == ConfigGuiTab.TABS) {
                GuiBase.openGui(new GuiTabManager());
            } else if (this.tab == ConfigGuiTab.CHAT_SUGGESTOR) {
                GuiBase.openGui(new GuiSuggesterConfig());
            } else {
                this.parent.reCreateListWidget(); // apply the new config width
                this.parent.getListWidget().resetScrollbarPosition();
                this.parent.initGui();
            }
        }
    }


    public enum ConfigGuiTab {
        GENERAL("general"),
        CHAT_SCREEN("chatscreen"),
        CHAT_LOG("chatlog"),
        CHAT_SUGGESTOR("chatsuggestor"),
        TABS("tabs"),
        FILTERS("filters");

        private final String name;

        ConfigGuiTab(String name) {
            this.name = name;
        }

        private static String translate(String key) {
            return StringUtils.translate("advancedchat.config.tab." + key);
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return translate(this.name);
        }
    }
}
