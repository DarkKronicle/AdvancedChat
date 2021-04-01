package net.darkkronicle.advancedchat.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.config.ChatTab;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.config.Filter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class SharingScreen extends GuiBase {

    private final String starting;
    private static final Gson GSON = new GsonBuilder().create();
    private GuiTextFieldGeneric text;

    public SharingScreen(String starting, Screen parent) {
        this.setParent(parent);
        this.setTitle(StringUtils.translate("advancedchat.gui.menu.import"));
        this.starting = starting;
    }

    public static SharingScreen fromFilter(Filter filter, Screen parent) {
        Filter.FilterJsonSave filterJsonSave = new Filter.FilterJsonSave();
        return new SharingScreen(GSON.toJson(filterJsonSave.save(filter)), parent);
    }

    public static SharingScreen fromTab(ChatTab tab, Screen parent) {
        ChatTab.ChatTabJsonSave tabJsonSave = new ChatTab.ChatTabJsonSave();
        return new SharingScreen(GSON.toJson(tabJsonSave.save(tab)), parent);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        int x = this.width / 2 - 150;
        int y = 50;
        text = new GuiTextFieldGeneric(x, y, 300, 20, client.textRenderer);
        y -= 24;
        text.setMaxLength(12800);
        if (starting != null) {
            text.setText(starting);
            text.setTextFieldFocused(true);
        }
        text.changeFocus(true);
        text.setDrawsBackground(true);
        text.setEditable(true);
        text.changeFocus(true);
        this.addTextField(text, null);
        this.addButton(new ButtonGeneric(x, y, 50, 22, ButtonListener.Type.IMPORT_FILTER.getDisplayName()), new ButtonListener(ButtonListener.Type.IMPORT_FILTER, this));
        this.addButton(new ButtonGeneric(x + 52, y, 50, 22, ButtonListener.Type.IMPORT_TAB.getDisplayName()), new ButtonListener(ButtonListener.Type.IMPORT_TAB, this));
    }

    private static class ButtonListener implements IButtonActionListener {

        public enum Type {
            IMPORT_FILTER("importfilter"),
            IMPORT_TAB("importtab")
            ;

            public final String translationString;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            Type(String key) {
                this.translationString = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translationString);
            }

        }

        private final Type type;
        private final SharingScreen parent;

        public ButtonListener(Type type, SharingScreen parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            try {
                if (parent.text.getText().equals("")) {
                    throw new NullPointerException("Message can't be blank!");
                }
                if (type == Type.IMPORT_FILTER) {
                    Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();
                    // If you don't use deprecated it won't work
                    Filter filter = filterSave.load(new JsonParser().parse(parent.text.getText()).getAsJsonObject());
                    if (filter == null) {
                        throw new NullPointerException("Filter is null!");
                    }
                    ConfigStorage.FILTERS.add(filter);
                    ChatDispatcher.getInstance().loadFilters();
                    parent.addGuiMessage(Message.MessageType.SUCCESS, 5000, StringUtils.translate("advancedchat.gui.message.successful"));
                } else if (type == Type.IMPORT_TAB) {
                    ChatTab.ChatTabJsonSave tabSave = new ChatTab.ChatTabJsonSave();
                    ChatTab tab = tabSave.load(new JsonParser().parse(parent.text.getText()).getAsJsonObject());
                    if (tab == null) {
                        throw new NullPointerException("Filter is null!");
                    }
                    ConfigStorage.TABS.add(tab);
                    AdvancedChat.chatTab.setUpTabs();
                    parent.addGuiMessage(Message.MessageType.SUCCESS, 5000, StringUtils.translate("advancedchat.gui.message.successful"));
                }
            } catch (Exception e) {
                parent.addGuiMessage(Message.MessageType.ERROR, 10000, StringUtils.translate("advancedchat.gui.message.error") + ": " + e.getMessage());
            }
        }
    }

}
