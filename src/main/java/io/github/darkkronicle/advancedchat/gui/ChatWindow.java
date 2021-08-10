package io.github.darkkronicle.advancedchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.chat.ChatHistory;
import io.github.darkkronicle.advancedchat.chat.ChatMessage;
import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.EasingMethod;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.LimitedInteger;
import io.github.darkkronicle.advancedchat.util.RawText;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChatWindow {

    private int scrolledLines = 0;
    @Getter
    private int y;
    @Getter
    private int x;
    @Getter
    private int width;
    @Getter
    private int height;
    private final MinecraftClient client;

    private ConfigStorage.Visibility visibility = (ConfigStorage.Visibility) ConfigStorage.ChatScreen.VISIBILITY.config.getOptionListValue();

    private List<ChatMessage> lines;

    @Getter
    @Setter
    private boolean selected;

    @Getter
    private AbstractChatTab tab;

    private final static Identifier X_ICON = new Identifier(AdvancedChat.MOD_ID, "textures/gui/chatwindow/x_icon.png");

    public ChatWindow(AbstractChatTab tab) {
        this.client = MinecraftClient.getInstance();
        this.y = client.getWindow().getScaledHeight() - ConfigStorage.ChatScreen.Y.config.getIntegerValue();
        this.x = ConfigStorage.ChatScreen.X.config.getIntegerValue();
        this.width = ConfigStorage.ChatScreen.WIDTH.config.getIntegerValue();
        this.height = ConfigStorage.ChatScreen.HEIGHT.config.getIntegerValue();
        this.setTab(tab);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setTab(AbstractChatTab tab) {
        this.tab = tab;
        this.lines = new ArrayList<>();
        List<ChatMessage> messages = ChatHistory.getInstance().getMessages();
        for (int i = messages.size() - 1; i >= 0; i--) {
            addMessage(messages.get(i));
        }
    }

    public void addMessage(ChatMessage message) {
        this.addMessage(message, false, false);
    }

    public void addMessage(ChatMessage message, boolean force, boolean updateCreation) {
        if (force || message.getTabs().contains(tab)) {
            ChatMessage newMessage = message.shallowClone(getPaddedWidth());
            newMessage.setCreationTick(MinecraftClient.getInstance().inGameHud.getTicks());
            this.lines.add(0, newMessage);
            if (scrolledLines > 0) {
                scrolledLines++;
            }
            int visibleMessagesMaxSize = ConfigStorage.ChatScreen.STORED_LINES.config.getIntegerValue();
            while(this.lines.size() > visibleMessagesMaxSize) {
                this.lines.remove(this.lines.size() - 1);
            }
        }
    }

    public void scroll(double amount) {
        this.scrolledLines = (int)((double) this.scrolledLines + amount);
        if (this.scrolledLines > lines.size()) {
            this.scrolledLines = lines.size();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
        }
    }

    public void resetScroll() {
        this.scrolledLines = 0;
    }

    public int getPaddedWidth() {
        return getScaledWidth() - ConfigStorage.ChatScreen.LEFT_PAD.config.getIntegerValue() - ConfigStorage.ChatScreen.RIGHT_PAD.config.getIntegerValue() - headOffset();
    }

    private int headOffset() {
        return ConfigStorage.General.CHAT_HEADS.config.getBooleanValue() ? 10 : 0;
    }

    private int getActualY(int y) {
        return (int) Math.ceil(this.y / getScale()) - y;
    }

    private int getLeftX() {
        return (int) Math.ceil(x / getScale());
    }

    private int getPaddedLeftX() {
        return getLeftX() + (int) Math.ceil(ConfigStorage.ChatScreen.LEFT_PAD.config.getIntegerValue() + headOffset() / getScale());
    }

    private double getScale() {
        return ConfigStorage.ChatScreen.CHAT_SCALE.config.getDoubleValue();
    }

    private int getRightX() {
        return getLeftX() + getScaledWidth();
    }

    private int getPaddedRightX() {
        return getRightX() - ConfigStorage.ChatScreen.RIGHT_PAD.config.getIntegerValue();
    }

    public int getActualHeight() {
        return height + getBarHeight();
    }

    private int getScaledHeight() {
        return (int) Math.ceil(height / getScale());
    }

    private int getScaledWidth() {
        return (int) Math.ceil(width / getScale());
    }

    private int getBarHeight() {
        return 14;
    }

    private int getScaledBarHeight() {
        return (int) Math.ceil(14 * getScale());
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return x <= mouseX && x + width >= mouseX && y >= mouseY && y - getActualHeight() <= mouseY;
    }

    public void render(MatrixStack matrixStack, int ticks, boolean focused) {
        if (visibility == ConfigStorage.Visibility.FOCUSONLY && !focused) {
            return;
        }

        int lineCount = lines.size();

        boolean chatFocused = visibility == ConfigStorage.Visibility.ALWAYS || focused;

        if (scrolledLines > lineCount) {
            scrolledLines = lineCount;
        }

        matrixStack.push();
        matrixStack.scale((float) getScale(), (float) getScale(), 1);

        int lines = 0;
        int renderedLines = 0;
        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();
        int leftX = getLeftX();
        int padLX = getPaddedLeftX();
        int rightX = getRightX();
        int padRX = getPaddedRightX();
        LimitedInteger y = new LimitedInteger(getScaledHeight() - ConfigStorage.ChatScreen.TOP_PAD.config.getIntegerValue(), ConfigStorage.ChatScreen.BOTTOM_PAD.config.getIntegerValue());

        for (int j = 0; j < this.lines.size(); j++) {
            ChatMessage message = this.lines.get(j)  ;
            // To get the proper index of reversed
            for (int i = message.getLineCount() - 1; i >= 0; i--) {
                int lineIndex = message.getLineCount() - i - 1;
                lines++;
                if (lines < scrolledLines) {
                    continue;
                }
                if (!y.incrementIfPossible(ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue())) {
                    break;
                }
                ChatMessage.AdvancedChatLine line = message.getLines().get(i);
                drawLine(matrixStack, line, leftX, y.getValue(), padLX, padRX, lineIndex, j, renderedLines, chatFocused, ticks);
                renderedLines++;
            }
            if (lines >= scrolledLines) {
                if (lines == lineCount) {
                    break;
                }
                if (!y.isPossible(ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue() + ConfigStorage.ChatScreen.MESSAGE_SPACE.config.getIntegerValue()) || !y.incrementIfPossible(ConfigStorage.ChatScreen.MESSAGE_SPACE.config.getIntegerValue())) {
                    break;
                }
            }
        }
        if (renderedLines == 0) {
            y.setValue(0);
        }

        if (focused) {
            if (isSelected()) {
                tab.resetUnread();
            }
            RenderUtils.drawOutline(leftX, getActualY(0) - scaledHeight - 1, scaledWidth, scaledHeight + 1, tab.getBorderColor().color());
            int scaledBar = getBarHeight();
            int newY = getScaledHeight() + scaledBar;
            String label = tab.getAbreviation();
            int labelWidth = StringUtils.getStringWidth(label) + 8;
            RenderUtils.drawRect(leftX, getActualY(newY), labelWidth, scaledBar, tab.getMainColor().color());
            RenderUtils.drawOutline(leftX, getActualY(newY), labelWidth, scaledBar, tab.getBorderColor().withAlpha(180).color());
            DrawableHelper.drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, tab.getAbreviation(), leftX + (labelWidth) / 2, getActualY(newY - 3), ColorUtil.WHITE.color());
            RenderUtils.drawRect(leftX + labelWidth, getActualY(newY), getScaledWidth() - labelWidth, scaledBar, selected ? tab.getMainColor().color() : tab.getInnerColor().color());
            RenderUtils.drawOutline(leftX + labelWidth, getActualY(newY), getScaledWidth() - labelWidth, scaledBar, tab.getBorderColor().color());

            RenderUtils.drawOutline(rightX - scaledBar, getActualY(newY), scaledBar, scaledBar, tab.getBorderColor().color());
            RenderUtils.drawOutline(rightX - scaledBar * 2 + 1, getActualY(newY), scaledBar, scaledBar, tab.getBorderColor().color());
            RenderUtils.drawOutline(rightX - scaledBar * 3 + 2, getActualY(newY), scaledBar, scaledBar, tab.getBorderColor().color());

            RenderUtils.color(1, 1, 1, 1);
            RenderUtils.bindTexture(X_ICON);
            DrawableHelper.drawTexture(matrixStack, rightX - scaledBar * 2 + 2, getActualY(newY - 1), scaledBar - 2, scaledBar - 2, 0, 0, 32, 32, 32, 32);

            RenderUtils.bindTexture(visibility.getTexture());
            DrawableHelper.drawTexture(matrixStack, rightX - scaledBar * 3 + 3, getActualY(newY - 1), scaledBar - 2, scaledBar - 2, 0, 0, 32, 32, 32, 32);
        }

        if (chatFocused) {
            drawRect(leftX, getActualY(y.getValue()), rightX, getActualY(getScaledHeight()), tab.getInnerColor().color());
            // Scroll bar
            float add = (float) (scrolledLines) / (lineCount + 1);
            int scrollHeight = (int) (add * getScaledHeight());
            RenderUtils.drawRect(getScaledWidth() + leftX - 1, getActualY(scrollHeight + 10), 1, 10, ColorUtil.WHITE.color());
        }
        matrixStack.pop();
    }

    private void drawLine(MatrixStack matrixStack, ChatMessage.AdvancedChatLine line, int x, int y, int pLX, int pRX, int lineIndex, int messageIndex, int renderedLines, boolean focused, int ticks) {
        int height = ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue();
        if (renderedLines == 0) {
            if (focused) {
                height += ConfigStorage.ChatScreen.BOTTOM_PAD.config.getIntegerValue();
            }
        } else if (lineIndex == 0) {
            height += ConfigStorage.ChatScreen.MESSAGE_SPACE.config.getIntegerValue();
            // Start of a line
        }
        ColorUtil.SimpleColor background = line.getParent().getBackground();
        ColorUtil.SimpleColor text = ConfigStorage.ChatScreen.EMPTY_TEXT_COLOR.config.getSimpleColor();
        if (background == null) {
            background = tab.getInnerColor();
        }
        if (messageIndex % 2 == 0 && ConfigStorage.ChatScreen.ALTERNATE_LINES.config.getBooleanValue()) {
            if (background.alpha() <= 215) {
                background = background.withAlpha(background.alpha() + 40);
            } else {
                background = background.withAlpha(background.alpha() - 40);
            }
        }
        float applied = 1;
        if (!focused) {
            // Find fade percentage
            int fadeStart = ConfigStorage.ChatScreen.FADE_START.config.getIntegerValue();
            int fadeStop = fadeStart + ConfigStorage.ChatScreen.FADE_TIME.config.getIntegerValue();
            int timeAlive = ticks - line.getParent().getCreationTick();
            float percent = (float) Math.min(1, (double) (timeAlive - fadeStart) / (double) (fadeStop - fadeStart));
            applied = 1 - (float) ((EasingMethod) ConfigStorage.ChatScreen.FADE_TYPE.config.getOptionListValue()).apply(percent);
            applied = Math.max(0, applied);
            if (applied <= 0) {
                return;
            }
            if (applied < 1) {
                // Adjust color for background and text due to fade
                background = ColorUtil.fade(background, applied);
                text = ColorUtil.fade(text, applied);
            }
        }

        // Get line
        Text render = line.getText();
        if (line.getParent().getStacks() > 0 && lineIndex == 0) {
            FluidText toPrint = new FluidText(render);
            Style style = Style.EMPTY;
            TextColor color = TextColor.fromRgb(ColorUtil.GRAY.color());
            style = style.withColor(color);
            toPrint.getRawTexts().add(new RawText(" (" + (line.getParent().getStacks() + 1) + ")", style));
            render = toPrint;
        }

        int backgroundWidth;

        if (!focused && ConfigStorage.ChatScreen.HUD_LINE_TYPE.config.getOptionListValue() == ConfigStorage.HudLineType.COMPACT) {
            backgroundWidth = client.textRenderer.getWidth(render) + 4 + headOffset();
        } else {
            backgroundWidth = getScaledWidth();
        }

        // Draw background
        RenderUtils.drawRect(x, getActualY(y), backgroundWidth, height, background.color());
        if (lineIndex == line.getParent().getLineCount() - 1 && line.getParent().getOwner() != null && ConfigStorage.General.CHAT_HEADS.config.getBooleanValue()) {
            RenderSystem.setShaderColor(1, 1, 1, applied);
            RenderSystem.setShaderTexture(0, line.getParent().getOwner().getTexture());
            DrawableHelper.drawTexture(matrixStack, pLX - 10, getActualY(y), 8, 8, 8, 8, 8, 8, 64, 64);
            DrawableHelper.drawTexture(matrixStack, pLX - 10, getActualY(y), 8, 8, 40, 8, 8, 8, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        client.textRenderer.drawWithShadow(matrixStack, render.asOrderedText(), pLX, getActualY(y) + 1, text.color());
    }

    public Style getText(double mouseX, double mouseY) {
        if (!AdvancedChatHud.getInstance().isChatFocused()) {
            return null;
        }
        double relX = mouseX;
        double relY = y - mouseY;
        double trueX = relX / getScale() - getPaddedLeftX();
        double trueY = relY / getScale();
        // Divide it by chat scale to get where it actually is
        if (trueX < 0.0D || trueY < 0.0D) {
            return null;
        }
        if (trueY > getScaledHeight() || trueX > getScaledWidth()) {
            return null;
        }

        int lines = 0;
        int lineCount = this.lines.size();
        LimitedInteger y = new LimitedInteger(getScaledHeight(), ConfigStorage.ChatScreen.BOTTOM_PAD.config.getIntegerValue());
        for (ChatMessage message : this.lines) {
            // To get the proper index of reversed
            for (int i = message.getLineCount() - 1; i >= 0; i--) {
                lines++;
                if (lines < scrolledLines) {
                    continue;
                }
                if (!y.incrementIfPossible(ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue())) {
                    break;
                }
                if (trueY <= y.getValue() && trueY >= y.getValue() - ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue()) {
                    ChatMessage.AdvancedChatLine line = message.getLines().get(i);
                    return this.client.textRenderer.getTextHandler().getStyleAt(line.getText(), (int) trueX);
                }
            }
            if (lines >= scrolledLines) {
                if (lines == lineCount) {
                    break;
                }
                if (!y.isPossible(ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue() + ConfigStorage.ChatScreen.MESSAGE_SPACE.config.getIntegerValue()) || !y.incrementIfPossible(ConfigStorage.ChatScreen.MESSAGE_SPACE.config.getIntegerValue())) {
                    break;
                }
            }
        }
        return null;
    }

    private static void drawRect(int x1, int y1, int x2, int y2, int color) {
        if (y1 > y2) {
            int med = y2;
            y2 = y1;
            y1 = med;
        }
        if (x1 > x2) {
            int med = x2;
            x2 = x1;
            x1 = med;
        }
        RenderUtils.drawRect(x1, y1, x2 - x1, y2 - y1, color);
    }

    public boolean isMouseOverDragBar(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) && mouseX <= x + width - (getScaledBarHeight() * 3) && mouseY <= y - height;
    }

    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        boolean onButtons = isMouseOverDragBar(mouseX - (getScaledBarHeight() * 2), mouseY) && mouseX >= x + width - getScaledBarHeight() * 3;
        if (!onButtons) {
            return false;
        }
        int x = width - (int) (mouseX - this.x);
        if (x >= 13 && x <= 26) {
            AdvancedChatHud.getInstance().deleteWindow(this);
        } else if (x >= 26) {
            visibility = visibility.cycle(true);
        }
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }

    public boolean isMouseOverResize(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) && mouseX >= x + width - (getScaledBarHeight()) && mouseY <= y - height;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        for (ChatMessage m : lines) {
            m.formatChildren(this.width);
        }
    }

    public void stackMessage(ChatMessage message) {
        ChatMessage toRemove = null;
        for (ChatMessage line : lines) {
            if (message.isSimilar(line)) {
                if (!ConfigStorage.General.CHAT_STACK_UPDATE.config.getBooleanValue()) {
                    // Just update the message and don't resend it
                    line.setStacks(message.getStacks());
                    return;
                }
                toRemove = line;
                break;
            }
        }
        if (toRemove != null) {
            // Remove and then readd it with the updated stack information
            lines.remove(toRemove);
            addMessage(message, true, true);
        }
    }

    public void clearLines() {
        this.lines.clear();
    }
}
