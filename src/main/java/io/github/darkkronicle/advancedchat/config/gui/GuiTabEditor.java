package io.github.darkkronicle.advancedchat.config.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.config.ChatTab;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.gui.SharingScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class GuiTabEditor extends GuiConfigsBase {
    private final ChatTab tab;

    public GuiTabEditor(Screen parent, ChatTab tab) {
        super(10, 50, AdvancedChat.MOD_ID, null, tab.getName().config.getStringValue());
        this.tab = tab;
        this.setParent(parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        createButtons(10, 26);
    }

    private void createButtons(int x, int y) {
        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        int topx = x;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(back, new ButtonListener(ButtonListener.Type.BACK, this));
        topx += back.getWidth() + 2;

        String exportText = ButtonListener.Type.EXPORT.getDisplayName();
        int exportWidth = StringUtils.getStringWidth(exportText) + 10;
        ButtonGeneric export = new ButtonGeneric(topx + exportWidth, y, exportWidth, true, exportText);
        this.addButton(export, new ButtonListener(ButtonListener.Type.EXPORT, this));
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        ArrayList<IConfigBase> config = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> s : tab.getOptions()) {
            config.add(s.config);
        }

        return ConfigOptionWrapper.createFor(config);
    }

    @Override
    public void onClose() {
        save();
        super.onClose();
    }

    @Override
    protected void closeGui(boolean showParent) {
        // Save the changes :)
        save();
        super.closeGui(showParent);
    }

    public void save() {
        AdvancedChat.chatTab.setUpTabs();
    }

    public void back() {
        closeGui(true);
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiTabEditor parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiTabEditor parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            } else if (this.type == ButtonListener.Type.EXPORT) {
                parent.save();
                GuiBase.openGui(SharingScreen.fromTab(parent.tab, parent));
            }
        }

        public enum Type {
            BACK("back"),
            EXPORT("export")
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
