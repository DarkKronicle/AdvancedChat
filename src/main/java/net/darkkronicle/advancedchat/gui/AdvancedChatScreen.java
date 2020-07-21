/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package net.darkkronicle.advancedchat.gui;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ModMenuImpl;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.gui.tabs.CustomChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class AdvancedChatScreen extends Screen {
    private String field_2389 = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText = "";
    private CommandSuggestor commandSuggestor;

    public AdvancedChatScreen(String originalChatText) {
        super(NarratorManager.EMPTY);
        this.originalChatText = originalChatText;
        if (this.originalChatText.isEmpty()) {
            AbstractChatTab tab = AdvancedChat.getAdvancedChatHud().getCurrentTab();
            if (tab instanceof CustomChatTab) {
                CustomChatTab customTab = (CustomChatTab) tab;
                this.originalChatText = customTab.getStartingMessage();
            }
        }
    }

    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, new TranslatableText("chat.editBox")) {
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(AdvancedChatScreen.this.commandSuggestor.method_23958());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setHasBorder(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.children.add(this.chatField);


        this.commandSuggestor = new CommandSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
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
        this.client.keyboard.enableRepeatEvents(false);
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
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 256) {
            this.client.openScreen(null);
            return true;
        } else if (keyCode != 257 && keyCode != 335) {
            if (keyCode == 265) {
                this.setChatFromHistory(-1);
                return true;
            } else if (keyCode == 264) {
                this.setChatFromHistory(1);
                return true;
            } else if (keyCode == 266) {
                AdvancedChat.getAdvancedChatHud().scroll((double)(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1));
                return true;
            } else if (keyCode == 267) {
                AdvancedChat.getAdvancedChatHud().scroll((double)(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1));
                return true;
            } else {
                return false;
            }
        } else {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }

            this.client.openScreen((Screen)null);
            return true;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        if (this.commandSuggestor.mouseScrolled(amount)) {
            return true;
        } else {
            if (!hasShiftDown()) {
                amount *= 7.0D;
            }

            AdvancedChat.getAdvancedChatHud().scroll(amount);
            return true;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestor.mouseClicked((double)((int)mouseX), (double)((int)mouseY), button)) {
            return true;
        } else {
            if (button == 0) {
                AdvancedChatHud chatHud = AdvancedChat.getAdvancedChatHud();

                Style style = chatHud.getText(mouseX, mouseY);
                if (style != null && this.handleTextClick(style)) {
                    return true;
                }

                int height = 11;
                int bottomOffset = AdvancedChat.configStorage.chatConfig.yOffset + AdvancedChat.configStorage.chatConfig.height + 5 + height;
                int y = client.getWindow().getScaledHeight() - bottomOffset;
                int width = AdvancedChat.configStorage.chatConfig.width + 4;
                if (isOverButton((int) mouseX + AdvancedChat.configStorage.chatConfig.xOffset, (int) mouseY, 0, y, width, height)) {
                    chatHud.cycleTab();
                }
                if (isOverButton((int) mouseX, (int) mouseY, client.getWindow().getScaledWidth() - 52, client.getWindow().getScaledHeight() - 27, 50, 11)) {
                    client.openScreen(new ChatLogScreen());
                }
                if (isOverButton((int) mouseX, (int) mouseY, client.getWindow().getScaledWidth() - 114, client.getWindow().getScaledHeight() - 27, 50, 11)) {
                    client.openScreen(ModMenuImpl.getScreen(this));
                }
            }

            return this.chatField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
        }
    }

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
                this.chatField.setText(this.field_2389);
            } else {
                if (this.messageHistorySize == k) {
                    this.field_2389 = this.chatField.getText();
                }

                this.chatField.setText((String)this.client.inGameHud.getChatHud().getMessageHistory().get(j));
                this.commandSuggestor.setWindowActive(false);
                this.messageHistorySize = j;
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();
        this.setFocused(this.chatField);
        this.chatField.setSelected(true);
        fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, AdvancedChat.configStorage.chatConfig.hudBackground.color());
        this.chatField.render(matrices, mouseX, mouseY, delta);
        this.commandSuggestor.render(matrices, mouseX, mouseY);
        Style style = hud.getText(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }
        renderChatTab(matrices, mouseX, mouseY, hud);

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderChatTab(MatrixStack matrices, int mouseX, int mouseY, AdvancedChatHud hud) {
        int width = AdvancedChat.configStorage.chatConfig.width + 4;
        int height = 11;
        int bottomOffset = AdvancedChat.configStorage.chatConfig.yOffset + AdvancedChat.configStorage.chatConfig.height + 5 + height;
        int y = client.getWindow().getScaledHeight() - bottomOffset;
        renderButton(matrices, mouseX, mouseY, AdvancedChat.configStorage.chatConfig.xOffset, y, width, height, hud.getCurrentTab().getName());
        renderButton(matrices, mouseX, mouseY, client.getWindow().getScaledWidth() - 60, client.getWindow().getScaledHeight() - 27, 50, 11, "Chat Log");
        renderButton(matrices, (int) mouseX, (int) mouseY, client.getWindow().getScaledWidth() - 114, client.getWindow().getScaledHeight() - 27, 50, 11, "Settings");
    }

    private void renderButton(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, String message) {
        ColorUtil.SimpleColor background = AdvancedChat.configStorage.chatConfig.hudBackground;
        if (mouseY >= y && mouseY <= y + height && mouseX >= x && mouseX <= x + width) {
            background = ColorUtil.WHITE.withAlpha(background.alpha());
        }
        fill(matrices, x, y, x + width, y + height, background.color());
        drawCenteredString(matrices, client.textRenderer, message, (width / 2) + x, y + (height - 9), AdvancedChat.configStorage.chatConfig.emptyText.color());
    }

    private boolean isOverButton(int mouseX, int mouseY, int x, int y, int width, int height) {
        if (mouseY >= y && mouseY <= y + height && mouseX >= x && mouseX <= x + width) {
            return true;
        }
        return false;
    }


    public boolean isPauseScreen() {
        return false;
    }


    private void setText(String text) {
        this.chatField.setText(text);
    }

}
