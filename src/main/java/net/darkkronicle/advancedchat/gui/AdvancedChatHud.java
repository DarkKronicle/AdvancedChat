package net.darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AdvancedChatHud extends DrawableHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final Deque<Text> queuedMessages = Queues.newArrayDeque();
    @Getter
    @Setter
    private AbstractChatTab currentTab;
    private int scrolledLines;
    private long lastTimeCheck = 0L;

    public AdvancedChatHud(MinecraftClient minecraftClient) {
        client = minecraftClient;
        currentTab = AdvancedChat.chatTab;
    }

    public void render(MatrixStack matrices, int tick) {
        double chatScale = this.getChatScale();
        // Set up rendering
        matrices.push();
        drawCenteredString(matrices, client.textRenderer, "Hello I hate this", 50, 30, ColorUtil.WHITE.color());
        matrices.scale((float) chatScale, (float) chatScale, 1);

        // Declare useful variables
        Window window = this.client.getWindow();
        int windowHeight = window.getScaledHeight();
        int actualWidth = MathHelper.ceil((double) getWidth() / this.getChatScale());
        int bottomScreenOffset = AdvancedChat.configStorage.chatConfig.yOffset;
        int lineHeight = AdvancedChat.configStorage.chatConfig.lineSpace;
        int maxSize = getHeight();
        int fadestart = 200;
        int fadestop = 240;

        // Check that tab is set...
        if (currentTab == null) {
            if (AdvancedChat.chatTab == null) {
                AdvancedChat.chatTab = new MainChatTab();
            }
            currentTab = AdvancedChat.chatTab;
        }
        ColorUtil.SimpleColor textColor = AdvancedChat.configStorage.chatConfig.emptyText;
        ColorUtil.SimpleColor backgroundColor = AdvancedChat.configStorage.chatConfig.hudBackground;
        boolean chatFocused = isChatFocused();
        if (currentTab.visibleMessages.size() > 0) {

            int lines = 0;

            if (scrolledLines >= currentTab.visibleMessages.size()) {
                scrolledLines = 0;
            }
            int finalheight = 0;
            for (int i = 0; i + scrolledLines < currentTab.visibleMessages.size(); i++) {
                AdvancedChatLine line = currentTab.visibleMessages.get(i + scrolledLines);
                lines++;

                int relativeHeight = (lines * lineHeight);
                int height = (windowHeight - bottomScreenOffset) - relativeHeight;
                if (relativeHeight <= maxSize) {
                    if (chatFocused) {
                        ColorUtil.SimpleColor fadebackground;
                        if (line.getBackground() != null) {
                            fadebackground = line.getBackground();
                        } else {
                            fadebackground = backgroundColor;
                        }
                        fill(matrices, 0, height, actualWidth + 4, height + lineHeight, fadebackground.color());
                        drawTextWithShadow(matrices, client.textRenderer, line.getText(), 1, height + 1, textColor.color());
                        finalheight = height;
                    } else {
                        int timeAlive = tick - line.getCreationTick();
                        if (timeAlive < fadestop) {
                            ColorUtil.SimpleColor fadebackground;
                            if (line.getBackground() != null) {
                                fadebackground = ColorUtil.fade(line.getBackground(), timeAlive, fadestart, fadestop);
                            } else {
                                fadebackground = ColorUtil.fade(backgroundColor, timeAlive, fadestart, fadestop);
                            }

                            ColorUtil.SimpleColor fadetext = ColorUtil.fade(textColor, timeAlive, fadestart, fadestop);
                            fill(matrices, 0, height, actualWidth + 4, height + lineHeight, fadebackground.color());
                            drawTextWithShadow(matrices, client.textRenderer, line.getText(), 1, height + 1, fadetext.color());
                        }
                    }
                }
            }
            if (chatFocused) {
                if (currentTab.visibleMessages.size() > 0) {
                    fill(matrices, 0, finalheight, actualWidth + 4, windowHeight - bottomScreenOffset - maxSize, backgroundColor.color());
                } else {
                    fill(matrices, 0, windowHeight - bottomScreenOffset, actualWidth + 4, windowHeight - bottomScreenOffset -  maxSize, backgroundColor.color());
                }
            }
        }


        matrices.pop();
    }

    public void messageAddedToTab(AbstractChatTab tab) {
        if (tab != currentTab) {
            return;
        }
        boolean chatFocused = this.isChatFocused();

        if (chatFocused && this.scrolledLines > 0) {
            this.scroll(1.0D);
        }
    }


    private boolean chatIsHidden() {
        return this.client.options.chatVisibility == ChatVisibility.HIDDEN;
    }

    public void clear(boolean clearHistory) {
        currentTab.visibleMessages.clear();
        currentTab.messages.clear();
        if (clearHistory) {
            this.messageHistory.clear();
        }

    }

   public void addMessage(Text message) {
        this.addMessage(message, 0);
    }

    public void addMessage(Text message, int messageId) {
        this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
        LOGGER.info("[CHAT] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    public void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl) {
        AdvancedChat.chatTab.addMessage(stringRenderable, messageId, timestamp, bl);

    }

    /**
     * When a chat option is changed, this resets all of the messages to be uniform together.
     * Avoids expensive SplitLines every tick this is rendered.
     */
    public void reset() {
        currentTab.reset();
        this.resetScroll();
    }

    public List<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String message) {
        if (this.messageHistory.isEmpty() || !((String)this.messageHistory.get(this.messageHistory.size() - 1)).equals(message)) {
            this.messageHistory.add(message);
        }

    }

    public void resetScroll() {
        this.scrolledLines = 0;
    }

    public void scroll(double amount) {
        this.scrolledLines = (int)((double)this.scrolledLines + amount);
        int i = currentTab.visibleMessages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
        }

    }

    public boolean getQueuedMessage(double d, double e) {
        if (this.isChatFocused() && !this.client.options.hudHidden && !this.chatIsHidden() && !this.queuedMessages.isEmpty()) {
            double f = d - 2.0D;
            double g = (double)this.client.getWindow().getScaledHeight() - e - 40.0D;
            if (f <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && g < 0.0D && g > (double)MathHelper.floor(-9.0D * this.getChatScale())) {
                this.addMessage((Text)this.queuedMessages.remove());
                this.lastTimeCheck = System.currentTimeMillis();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //TODO AAAAAAAHHHH
    public Style getText(double mouseX, double mouseY) {
        if (this.isChatFocused() && !this.client.options.hudHidden && !this.chatIsHidden()) {
            double trueX = mouseX - 2;
            double trueY = (double)this.client.getWindow().getScaledHeight() - mouseY - AdvancedChat.configStorage.chatConfig.yOffset;
//            trueX = MathHelper.floor(trueX);
//            trueY = MathHelper.floor(trueY * (AdvancedChat.configStorage.chatConfig.lineSpace + 1.0D));
            if (trueX >= 0.0D && trueY >= 0.0D) {
                int numOfMessages = Math.min(this.getVisibleLineCount(), currentTab.visibleMessages.size());
                if (trueX <= (double) MathHelper.floor((double) getWidth())) {
                    if (trueY < (double)(9 * numOfMessages + numOfMessages)) {
                        int lineNum = (int)(trueY / AdvancedChat.configStorage.chatConfig.lineSpace + (double)this.scrolledLines);
                        if (lineNum >= 0 && lineNum < currentTab.visibleMessages.size()) {
                            AdvancedChatLine chatHudLine = currentTab.visibleMessages.get(lineNum);
                            return this.client.textRenderer.getTextHandler().trimToWidth(chatHudLine.getText(), (int)trueX);
                        }
                    }
                }

            }
            return null;
        } else {
            return null;
        }
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof AdvancedChatScreen;
    }

    public void removeMessage(int messageId) {
        currentTab.removeMessage(messageId);
    }

    public static int getWidth() {
        return AdvancedChat.configStorage.chatConfig.width;
    }

    public double getChatScale() {
        return 1;
    }

    public static int getHeight() {
        return AdvancedChat.configStorage.chatConfig.height;
    }

    public int getVisibleLineCount() {
        return AdvancedChatHud.getHeight() / AdvancedChat.configStorage.chatConfig.lineSpace;
    }

    private long getChatDelayMS() {
        return (long)(this.client.options.chatDelay * 1000.0D);
    }

    private void processQueuedMessages() {
        if (!this.queuedMessages.isEmpty()) {
            long l = System.currentTimeMillis();
            if (l - this.lastTimeCheck >= this.getChatDelayMS()) {
                this.addMessage((Text)this.queuedMessages.remove());
                this.lastTimeCheck = l;
            }

        }
    }

    public void addQueuedMessage(Text text) {
        if (this.client.options.chatDelay <= 0.0D) {
            this.addMessage(text);
        } else {
            long l = System.currentTimeMillis();
            if (l - this.lastTimeCheck >= this.getChatDelayMS()) {
                this.addMessage(text);
                this.lastTimeCheck = l;
            } else {
                this.queuedMessages.add(text);
            }
        }

    }

    public void cycleTab() {
        ArrayList<AbstractChatTab> tabs = AdvancedChat.chatTab.getAllChatTabs();
        if (tabs.size() <= 0) {
            return;
        }
        int cur = tabs.indexOf(this.getCurrentTab()) + 1;
        if (cur >= tabs.size()) {
            cur = 0;
        }
        AbstractChatTab newtab = tabs.get(cur);
        this.setCurrentTab(newtab);
    }
}
