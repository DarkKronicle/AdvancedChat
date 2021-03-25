package net.darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.AdvancedChatMessage;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

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
        // TODO fix garbage

        // Set up rendering
        matrices.push();
        matrices.scale((float) getChatScale(), (float) getChatScale(), 1);

        // Declare useful variables
        Window window = this.client.getWindow();
        boolean heads = ConfigStorage.General.CHAT_HEADS.config.getBooleanValue();
        // How far heads will render
        int headoffset = heads ? 10 : 0;
        // Width shouldn't be affected of chat scale
        int windowHeight = MathHelper.ceil(window.getScaledHeight() / getChatScale());
        int actualWidth = MathHelper.ceil((double) getWidth() / getChatScale()) + headoffset;
        int actualHeight = MathHelper.ceil((double) getHeight() / getChatScale());
        // Where text will start
        int xoffset = ConfigStorage.ChatScreen.X.config.getIntegerValue() + headoffset;
        // Where the background will be drawn
        int fillX = xoffset - headoffset;
        int bottomScreenOffset = MathHelper.ceil(ConfigStorage.ChatScreen.Y.config.getIntegerValue() / getChatScale());
        int lineHeight = ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue();
        int maxSize = actualHeight;
        int fadestart = ConfigStorage.ChatScreen.FADE_START.config.getIntegerValue();
        int fadestop = fadestart + ConfigStorage.ChatScreen.FADE_TIME.config.getIntegerValue();
        EasingMethod ease = ConfigStorage.Easing.fromEasingString(ConfigStorage.ChatScreen.FADE_TYPE.config.getStringValue());

        // Check that tab is set...
        if (currentTab == null) {
            if (AdvancedChat.chatTab == null) {
                AdvancedChat.chatTab = new MainChatTab();
            }
            currentTab = AdvancedChat.chatTab;
        }

        ColorUtil.SimpleColor textColor = ConfigStorage.ChatScreen.EMPTY_TEXT_COLOR.config.getSimpleColor();
        ColorUtil.SimpleColor backgroundColor = ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.getSimpleColor();
        ColorUtil.SimpleColor ogcolor = backgroundColor;

        ConfigStorage.Visibility visibility = ConfigStorage.Visibility.fromVisibilityString(ConfigStorage.ChatScreen.VISIBILITY.config.getStringValue());
        boolean chatFocused = visibility == ConfigStorage.Visibility.ALWAYS || isChatFocused();
        if (visibility == ConfigStorage.Visibility.FOCUSONLY && !chatFocused) {
            return;
        }

        ConfigStorage.HudLineType hudLineType = ConfigStorage.HudLineType.fromHudLineTypeString(ConfigStorage.ChatScreen.HUD_LINE_TYPE.config.getStringValue());

        // How far up we went. Used to fill in the rest of the box.
        int finalheight = 0;

        int lineCount = currentTab.getLineCount();
        if (lineCount > 0) {

            // Current line number
            int lines = 0;
            boolean alternate = ConfigStorage.ChatScreen.ALTERNATE_LINES.config.getBooleanValue();
            boolean didAlternate = false;
            // Check to see if the scroll is too far.
            if (scrolledLines >= lineCount) {
                scrolledLines = 0;
            }
            if (chatFocused) {
                // Scroll bar
                float add = (float) scrolledLines / (lineCount - getVisibleLineCount() + 1);
                int scrollHeight = (int) (add * maxSize);
                fill(matrices, actualWidth + 3 + xoffset, windowHeight - bottomScreenOffset - scrollHeight, actualWidth + 4 + xoffset, windowHeight - bottomScreenOffset - scrollHeight - 10, ColorUtil.WHITE.color());
            }

            // Render each message
            ArrayList<UUID> rendered = new ArrayList<>();
            for (int i = 0; i + scrolledLines < lineCount; i++) {
                // TODO big bug with messages that are over 1 line don't scroll itself correctly
                AdvancedChatMessage line = currentTab.getMessageFromLine(i + scrolledLines);
                if (rendered.contains(line.getUuid())) {
                    continue;
                }
                if (alternate) {
                    didAlternate = !didAlternate;
                }
                if (!alternate || didAlternate) {
                    backgroundColor = ogcolor;
                } else {
                    if (backgroundColor.alpha() <= 215) {
                        backgroundColor = backgroundColor.withAlpha(backgroundColor.alpha() + 40);
                    } else {
                        backgroundColor = backgroundColor.withAlpha(backgroundColor.alpha() - 40);
                    }
                }
                rendered.add(line.getUuid());
                boolean showStack = true;
                ArrayList<AdvancedChatMessage.AdvancedChatLine> chatLines = line.getLines();
                for (int j = chatLines.size() - 1; j >= 0; j--) {
                    AdvancedChatMessage.AdvancedChatLine l = chatLines.get(j);
                    lines++;
                    int relativeHeight = (lines * lineHeight);
                    int height = (windowHeight - bottomScreenOffset) - relativeHeight;
                    // Only show head on the top line
                    boolean headNow = heads && j == 0;
                    if (relativeHeight <= maxSize) {
                        if (chatFocused) {
                            ColorUtil.SimpleColor fadebackground;
                            if (line.getBackground() != null) {
                                fadebackground = line.getBackground();
                            } else {
                                fadebackground = backgroundColor;
                            }
                            fill(matrices, fillX, height, fillX + actualWidth + 4, height + lineHeight, fadebackground.color());
                            Text newString = l.getText();
                            if (line.getStacks() > 0 && showStack) {
                                SplitText toPrint = new SplitText(newString);
                                Style style = Style.EMPTY;
                                TextColor color = TextColor.fromRgb(ColorUtil.GRAY.color());
                                style = style.withColor(color);
                                toPrint.getSiblings().add(new SimpleText(" (" + line.getStacks() + ")", style));
                                newString = toPrint.getText();
                                showStack = false;
                            }

                            if (headNow && line.getOwner() != null) {
                                client.getTextureManager().bindTexture(line.getOwner().getTexture());
                                DrawableHelper.drawTexture(matrices, fillX + 1, height, 8, 8, 8, 8, 8, 8, 64, 64);
                            }
                            drawTextWithShadow(matrices, client.textRenderer, newString, xoffset + 1, height + 1, textColor.color());
                            finalheight = height;
                        } else {
                            int timeAlive = tick - line.getCreationTick();
                            if (timeAlive < fadestop) {
                                float perc = (float) Math.min(1, 1 - ease.apply((double) (timeAlive - fadestart) / (double) (fadestop - fadestart)));
                                ColorUtil.SimpleColor fadebackground;
                                // Fade background and text.
                                if (line.getBackground() != null) {
                                    fadebackground = ColorUtil.fade(line.getBackground(), perc);
                                } else {
                                    fadebackground = ColorUtil.fade(backgroundColor, perc);
                                }
                                ColorUtil.SimpleColor fadetext = ColorUtil.fade(textColor, perc);
                                // If alpha is super low it renders it at 255?
                                // Thanks minecraft -_-
                                if (fadetext.alpha() <= 5) {
                                    continue;
                                }
                                if (hudLineType == ConfigStorage.HudLineType.FULL) {
                                    fill(matrices, fillX, height, fillX + actualWidth + 4, height + lineHeight, fadebackground.color());
                                } else if (hudLineType == ConfigStorage.HudLineType.COMPACT) {
                                    int width = l.getWidth();
                                    fill(matrices, fillX, height, fillX + width + headoffset + 2, height + lineHeight, fadebackground.color());
                                }
                                Text newString = l.getText();
                                if (line.getStacks() > 0 && showStack) {
                                    SplitText toPrint = new SplitText(newString);
                                    Style style = Style.EMPTY;
                                    TextColor color = TextColor.fromRgb(ColorUtil.GRAY.color());
                                    style = style.withColor(color);
                                    toPrint.getSiblings().add(new SimpleText(" (" + line.getStacks() + ")", style));
                                    newString = toPrint.getText();
                                    showStack = false;
                                }
                                if (headNow && line.getOwner() != null) {
                                    RenderSystem.color4f(1, 1, 1, (float) textColor.alpha() / 255);
                                    client.getTextureManager().bindTexture(line.getOwner().getTexture());
                                    DrawableHelper.drawTexture(matrices, fillX + 1, height, 8, 8, 8, 8, 8, 8, 64, 64);
                                    RenderSystem.color4f(1, 1, 1, 1);
                                }
                                drawTextWithShadow(matrices, client.textRenderer, newString, xoffset + 1, height + 1, fadetext.color());
                            }
                        }
                    }
                }
            }

        }
        if (chatFocused) {
            if (currentTab.messages.size() > 0) {
                fill(matrices, fillX, finalheight, fillX + actualWidth + 4, windowHeight - bottomScreenOffset - maxSize, backgroundColor.color());
            } else {
                fill(matrices, fillX, windowHeight - bottomScreenOffset, fillX + actualWidth + 4, windowHeight - bottomScreenOffset -  maxSize, backgroundColor.color());
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
        for (AbstractChatTab tab : AdvancedChat.chatTab.getAllChatTabs()) {
            tab.messages.clear();
        }
        if (clearHistory) {
            this.messageHistory.clear();
        }

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
        int i = currentTab.messages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
        }

    }


    public Style getText(double mouseX, double mouseY) {
        if (!this.isChatFocused() || this.client.options.hudHidden || this.chatIsHidden()) {
            return null;
        }
        double relX = mouseX - 2 - ConfigStorage.ChatScreen.X.config.getIntegerValue();
        if (ConfigStorage.General.CHAT_HEADS.config.getBooleanValue()) {
            relX += 10;
        }
        double relY = (double)this.client.getWindow().getScaledHeight() - mouseY - ConfigStorage.ChatScreen.Y.config.getIntegerValue();
        double trueX = relX / getChatScale();
        double trueY = relY / getChatScale();
        // Divide it by chat scale to get where it actually is
        if (trueX < 0.0D || trueY < 0.0D) {
            return null;
        }

        int numOfMessages = Math.min(this.getVisibleLineCount(), currentTab.getLineCount());
        if (trueX > (double) MathHelper.floor((double) getWidth())) {
            return null;
        }
        if (trueY > (double) (9 * numOfMessages + numOfMessages)) {
            return null;
        }

        int lineNum = (int)(trueY / ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue() + (double) this.scrolledLines);
        if (lineNum >= 0 && lineNum < currentTab.getLineCount() && lineNum <= getVisibleLineCount() + scrolledLines) {
            AdvancedChatMessage.AdvancedChatLine chatHudLine = currentTab.getLine(lineNum);
            return this.client.textRenderer.getTextHandler().getStyleAt(chatHudLine.getText(), (int)trueX);
        }

        return null;
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof AdvancedChatScreen;
    }

    public void removeMessage(int messageId) {
        currentTab.removeMessage(messageId);
    }

    public static int getWidth() {
        return ConfigStorage.ChatScreen.WIDTH.config.getIntegerValue();
    }

    public static int getScaledWidth() {
        return (int) Math.ceil(getWidth() / getChatScale());
    }

    public static int getScaledHeight() {
        return (int) Math.ceil(getHeight() / getChatScale());
    }

    public static double getChatScale() {
        return ConfigStorage.ChatScreen.CHAT_SCALE.config.getDoubleValue();
    }

    public static int getHeight() {
        return ConfigStorage.ChatScreen.HEIGHT.config.getIntegerValue();
    }

    public int getVisibleLineCount() {
        return AdvancedChatHud.getScaledHeight() / ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue();
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
