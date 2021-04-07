package io.github.darkkronicle.advancedchat.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import io.github.darkkronicle.advancedchat.chat.tabs.CustomChatTab;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.gui.elements.CleanButton;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.config.gui.GuiConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class AdvancedChatScreen extends GuiBase {
    private String finalHistory = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText = "";
    private ChatSuggestorGui commandSuggestor;

    private static String last = "";

    @Override
    protected void closeGui(boolean showParent) {
        if (ConfigStorage.ChatScreen.PERSISTENT_TEXT.config.getBooleanValue()) {
            last = chatField.getText();
        }
        super.closeGui(showParent);
    }

    public AdvancedChatScreen(String originalChatText) {
        super();
        this.originalChatText = originalChatText;
        if (this.originalChatText.isEmpty()) {
            AbstractChatTab tab = AdvancedChat.getAdvancedChatHud().getCurrentTab();
            if (tab instanceof CustomChatTab) {
                CustomChatTab customTab = (CustomChatTab) tab;
                this.originalChatText = customTab.getStartingMessage();
            }
        }
    }

    public void initGui() {
        super.initGui();
        this.client.keyboard.setRepeatEvents(true);
        this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, new TranslatableText("chat.editBox")) {
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(AdvancedChatScreen.this.commandSuggestor.getNarration());
            }
        };
        if (ConfigStorage.ChatScreen.MORE_TEXT.config.getBooleanValue()) {
            this.chatField.setMaxLength(64000);
        } else {
            this.chatField.setMaxLength(256);
        }
        this.chatField.setDrawsBackground(false);
        if (!this.originalChatText.equals("")) {
            this.chatField.setText(this.originalChatText);
        } else if (ConfigStorage.ChatScreen.PERSISTENT_TEXT.config.getBooleanValue() && !last.equals("")) {
            this.chatField.setText(last);
        }
        this.chatField.setChangedListener(this::onChatFieldUpdate);

        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();

        int width = ConfigStorage.ChatScreen.WIDTH.config.getIntegerValue();
        int height = 11;
        int bottomOffset = ConfigStorage.ChatScreen.Y.config.getIntegerValue() + ConfigStorage.ChatScreen.HEIGHT.config.getIntegerValue() + 5 + height;
        int y = client.getWindow().getScaledHeight() - bottomOffset;
        ColorUtil.SimpleColor baseColor = ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.getSimpleColor();
        CleanButton tabButton = new CleanButton(ConfigStorage.ChatScreen.X.config.getIntegerValue(), y, width, height, baseColor, hud.getCurrentTab().getName());
        this.addButton(tabButton, (button, mouseButton) -> {
            hud.cycleTab();
            button.setDisplayString(hud.getCurrentTab().getName());
        });
        int x = client.getWindow().getScaledWidth() - 1;
        String chatlog = StringUtils.translate("advancedchat.gui.button.chatlog");
        int chatlogWidth = StringUtils.getStringWidth(chatlog) + 5;
        x -= chatlogWidth + 2;
        CleanButton chatLogButton = new CleanButton(x, client.getWindow().getScaledHeight() - 27, chatlogWidth, 11, baseColor, chatlog);
        this.addButton(chatLogButton, (button, mouseButton) -> GuiBase.openGui(new ChatLogScreen()));
        String settings = StringUtils.translate("advancedchat.gui.button.settings");
        int settingsWidth = StringUtils.getStringWidth(settings) + 5;
        x -= settingsWidth + 5;
        CleanButton settingsButton = new CleanButton(x, client.getWindow().getScaledHeight() - 27, settingsWidth, 11, baseColor, settings);
        this.addButton(settingsButton, (button, mouseButton) -> GuiBase.openGui(new GuiConfig()));

        this.children.add(this.chatField);

        if (ConfigStorage.ChatScreen.SHOW_TABS.config.getBooleanValue()) {
            int xadd = width + 2;
            int yadd = client.getWindow().getScaledHeight() - 11 - ConfigStorage.ChatScreen.Y.config.getIntegerValue();
            int orgYadd = yadd;
            int tabchar = ConfigStorage.ChatScreen.TAB_SIDE_CHARS.config.getIntegerValue();
            int bwidth = tabchar * 10 + 2;
            int relativeHeight = 13;
            for (AbstractChatTab tab : AdvancedChat.chatTab.getAllChatTabs()) {
                if (relativeHeight >= ConfigStorage.ChatScreen.HEIGHT.config.getIntegerValue()) {
                    xadd = xadd + bwidth + 2;
                    yadd = orgYadd;
                    relativeHeight = 13;
                }
                String abrev;
                if (tab.getAbreviation().equals("") || tab.getAbreviation() == null) {
                    abrev = tab.getName();
                } else {
                    abrev = tab.getAbreviation();
                }
                if (abrev.length() >= tabchar) {
                    abrev = abrev.substring(0, tabchar);
                }
                CleanButton buttonTab = new CleanButton(xadd, yadd, bwidth, 11, baseColor, abrev);
                this.addButton(buttonTab, (button, mouseButton) -> {
                    hud.setCurrentTab(tab);
                    tabButton.setDisplayString(tab.getName());
                });
                yadd = yadd - 13;
                relativeHeight = relativeHeight + 13;
            }
        }


        this.commandSuggestor = new ChatSuggestorGui(this.client, this, this.chatField, this.textRenderer, false, false, 1, ConfigStorage.ChatSuggestor.SUGGESTION_SIZE.config.getIntegerValue(), true);
        this.commandSuggestor.refresh();
        this.setInitialFocus(this.chatField);

    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.chatField.getText();
        this.init(client, width, height);
        this.setText(string);
        this.commandSuggestor.refresh();
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        AdvancedChat.getAdvancedChatHud().resetScroll();
    }

    public void tick() {
        this.chatField.tick();
    }

    private void onChatFieldUpdate(String chatText) {
        String string = this.chatField.getText();
        this.commandSuggestor.setWindowActive(!string.equals(this.originalChatText));
        this.commandSuggestor.refresh();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == KeyCodes.KEY_ESCAPE) {
            this.client.openScreen(null);
            return true;
        }
        if (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_KP_ENTER) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                if (string.length() > 256) {
                    string = string.substring(0, 256);
                }
                this.sendMessage(string);
            }
            this.chatField.setText("");
            last = "";
            this.client.openScreen(null);
            return true;
        }
        if (keyCode == KeyCodes.KEY_UP) {
            this.setChatFromHistory(-1);
            return true;
        }
        if (keyCode == KeyCodes.KEY_DOWN) {
            this.setChatFromHistory(1);
            return true;
        }
        if (keyCode == KeyCodes.KEY_PAGE_UP) {
            AdvancedChat.getAdvancedChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        }
        if (keyCode == KeyCodes.KEY_PAGE_DOWN) {
            AdvancedChat.getAdvancedChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        if (!this.commandSuggestor.mouseScrolled(amount)) {
            if (!hasShiftDown()) {
                amount *= 7.0D;
            }

            AdvancedChat.getAdvancedChatHud().scroll(amount);
        }
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestor.mouseClicked((int) mouseX, (int) mouseY, button)) {
            return true;
        } else {
            if (button == 0) {
                AdvancedChatHud chatHud = AdvancedChat.getAdvancedChatHud();

                Style style = chatHud.getText(mouseX, mouseY);
                if (style != null && this.handleTextClick(style)) {
                    return true;
                }
            }
            return this.chatField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void insertText(String text, boolean override) {
        if (override) {
            this.chatField.setText(text);
        } else {
            this.chatField.write(text);
        }
    }

    public void setChatFromHistory(int i) {
        int j = this.messageHistorySize + i;
        int k = this.client.inGameHud.getChatHud().getMessageHistory().size();
        j = MathHelper.clamp(j, 0, k);
        if (j != this.messageHistorySize) {
            if (j == k) {
                this.messageHistorySize = k;
                this.chatField.setText(this.finalHistory);
            } else {
                if (this.messageHistorySize == k) {
                    this.finalHistory = this.chatField.getText();
                }

                this.chatField.setText(this.client.inGameHud.getChatHud().getMessageHistory().get(j));
                this.commandSuggestor.setWindowActive(false);
                this.messageHistorySize = j;
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();
        this.setFocused(this.chatField);
        this.chatField.setTextFieldFocused(true);
        fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.getSimpleColor().color());
        this.chatField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.commandSuggestor.render(matrixStack, mouseX, mouseY);
        Style style = hud.getText(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderTextHoverEffect(matrixStack, style, mouseX, mouseY);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY) {

    }

    public boolean isPauseScreen() {
        return false;
    }


    private void setText(String text) {
        this.chatField.setText(text);
    }

}
