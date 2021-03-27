package net.darkkronicle.advancedchat.config.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ChatTab;
import net.darkkronicle.advancedchat.config.gui.widgets.WidgetLabelHoverable;
import net.darkkronicle.advancedchat.config.gui.widgets.WidgetToggle;
import net.darkkronicle.advancedchat.gui.SharingScreen;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class GuiTabEditor extends GuiBase {

    public final ChatTab tab;

    private GuiTextFieldGeneric name;
    private GuiTextFieldGeneric findString;
    private GuiTextFieldGeneric startingMessage;
    private GuiTextFieldGeneric abbreviation;
    private WidgetToggle forward;

    public GuiTabEditor(ChatTab tab, Screen parent) {
        this.tab = tab;
        this.title = tab.getName().config.getStringValue();
        this.setParent(parent);
    }

    @Override
    protected void closeGui(boolean showParent) {
        // Save the changes :)
        save();
        super.closeGui(showParent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;
        createButtons(x, y);
    }

    private int getWidth() {
        return 300;
    }

    public void save() {
        tab.getName().config.setValueFromString(name.getText());
        tab.getFindString().config.setValueFromString(findString.getText());
        tab.getAbbreviation().config.setValueFromString(abbreviation.getText());
        tab.getStartingMessage().config.setValueFromString(startingMessage.getText());
        tab.getForward().config.setBooleanValue(forward.isCurrentlyOn());
        AdvancedChat.chatTab.setUpTabs();
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
        y += export.getHeight() + 2;

        y += this.addLabel(x, y, tab.getName().config) + 1;
        name = this.addStringConfigButton(x, y, getWidth(), 10, tab.getName().config);
        y += name.getHeight() + 4;
        y += this.addLabel(x, y, tab.getStartingMessage().config) + 1;
        startingMessage = this.addStringConfigButton(x, y, getWidth(), 10, tab.getStartingMessage().config);
        y += startingMessage.getHeight() + 4;

        this.addLabel(x + getWidth() / 2, y, tab.getForward().config);
        y += this.addLabel(x, y, tab.getAbbreviation().config) + 1;
        abbreviation = this.addStringConfigButton(x, y, getWidth() / 2 - 1, 18, tab.getAbbreviation().config);
        forward = new WidgetToggle(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, false, "advancedchat.config.tab.forwardtoggle", tab.getForward().config.getBooleanValue());
        this.addButton(forward, null);
        y += forward.getHeight() + 2;

        // Find
        this.addLabel(x + getWidth() / 2, y, tab.getFindType().config);
        y += this.addLabel(x, y, tab.getFindString().config) + 1;
        findString = this.addStringConfigButton(x, y, getWidth() / 2 - 1, 18, tab.getFindString().config);
        ConfigButtonOptionList findType = new ConfigButtonOptionList(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, 20, tab.getFindType().config);
        this.addButton(findType, null);
        y += findType.getHeight() + 2;

    }

    private int addLabel(int x, int y, IConfigBase config) {
        int width = StringUtils.getStringWidth(config.getConfigGuiDisplayName());
        WidgetLabelHoverable label = new WidgetLabelHoverable(x, y, width, 8, ColorUtil.WHITE.color(), config.getConfigGuiDisplayName());
        label.setHoverLines(StringUtils.translate(config.getComment()));
        this.addWidget(label);
        return 8;
    }

    private GuiTextFieldGeneric addStringConfigButton(int x, int y, int width, int height, ConfigString conf) {
        GuiTextFieldGeneric name = new GuiTextFieldGeneric(x, y, width, height, this.textRenderer);
        name.setMaxLength(128);
        name.setText(conf.getStringValue());
        this.addTextField(name, null);
        return name;
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
