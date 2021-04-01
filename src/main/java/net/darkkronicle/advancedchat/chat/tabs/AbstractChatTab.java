package net.darkkronicle.advancedchat.chat.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.chat.AdvancedChatMessage;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.EasingMethod;
import net.darkkronicle.advancedchat.util.LimitedInteger;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base ChatTab that allows for custom chat tabs in AdvancedChatHud.
 */
@Environment(EnvType.CLIENT)
@Data
public abstract class AbstractChatTab {

    // Each tab stores their own messages.
    public final List<AdvancedChatMessage> messages = new ArrayList<>();
    private final String name;
    private final String abreviation;
    private final AdvancedChatHud hud;
    private final MinecraftClient client;
    private int scrolledLines = 0;

    public AbstractChatTab(String name, String abreviation) {
        hud = AdvancedChat.getAdvancedChatHud();
        client = MinecraftClient.getInstance();
        this.name = name;
        this.abreviation = abreviation;
    }

    public int getLineCount() {
        int i = 0;
        for (AdvancedChatMessage message : messages) {
            i += message.getLineCount();
        }
        return i;
    }

    /**
     * If the inputted message should be put into the chat tab.
     *
     * @param text Object to search.
     * @return True if it should be added.
     */
    public abstract boolean shouldAdd(Text text);

    /**
     * Method to reformat the messages if General size changes or something.
     */
    public void reset() {
        int width = MathHelper.floor(getPaddedWidth());
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            this.messages.get(i).formatChildren(width);
        }
    }

    public AdvancedChatMessage.AdvancedChatLine getLine(int line) {
        int oldline = 0;
        for (AdvancedChatMessage m : messages) {
            int newline = oldline + m.getLineCount();
            if (oldline <= line && newline > line) {
                return m.getLines().get(line - oldline);
            }
            oldline = newline;
        }
        return null;
    }

    public AdvancedChatMessage getMessageFromLine(int line) {
        int oldline = 0;
        for (AdvancedChatMessage m : messages) {
            int newline = oldline + m.getLineCount();
            if (oldline <= line && newline > line) {
                return m;
            }
            oldline = newline;
        }
        return null;
    }

    public void addMessage(AdvancedChatMessage line) {
        if (!shouldAdd(line.getDisplayText())) {
            return;
        }
        // Whether or not to override one
        if (line.getId() != 0) {
            this.removeMessage(line.getId());
        }
        for (int i = 0; i < ConfigStorage.General.CHAT_STACK.config.getIntegerValue() && i < messages.size(); i++) {
            AdvancedChatMessage chatLine = messages.get(i);
            if (line.isSimilar(chatLine)) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }
        line = line.shallowClone(getPaddedWidth());
        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        this.messages.add(0, line);
        if (scrolledLines > 0) {
            scroll(1);
        }
        int visibleMessagesMaxSize = ConfigStorage.ChatScreen.STORED_LINES.config.getIntegerValue();
        while (this.messages.size() > visibleMessagesMaxSize) {
            this.messages.remove(this.messages.size() - 1);
        }
    }

    public void removeMessage(int messageId) {
        Iterator iterator = this.messages.iterator();

        AdvancedChatMessage chatHudLine2;
        while (iterator.hasNext()) {
            chatHudLine2 = (AdvancedChatMessage) iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
            }
        }
    }

    public void scroll(double amount) {
        this.scrolledLines = (int)((double) this.scrolledLines + amount);
        if (this.scrolledLines > this.getLineCount()) {
            this.scrolledLines = this.getLineCount();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
        }
    }

    public void resetScroll() {
        this.scrolledLines = 0;
    }

    public int getPaddedWidth() {
        return getScaledWidth() - ConfigStorage.ChatScreen.LEFT_PAD.config.getIntegerValue() - ConfigStorage.ChatScreen.RIGHT_PAD.config.getIntegerValue();
    }

    private int getActualY(int y) {
        return (int) Math.ceil((client.getWindow().getScaledHeight() - ConfigStorage.ChatScreen.Y.config.getIntegerValue()) / getScale()) - y;
    }

    private int getLeftX() {
        return (int) Math.ceil(ConfigStorage.ChatScreen.X.config.getIntegerValue() / getScale());
    }

    private int getPaddedLeftX() {
        return getLeftX() + (int) Math.ceil(ConfigStorage.ChatScreen.LEFT_PAD.config.getIntegerValue() + (ConfigStorage.General.CHAT_HEADS.config.getBooleanValue() ? 10 : 0) / getScale());
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

    private int getScaledHeight() {
        return (int) Math.ceil(ConfigStorage.ChatScreen.HEIGHT.config.getIntegerValue() / getScale());
    }

    private int getScaledWidth() {
        return (int) Math.ceil(ConfigStorage.ChatScreen.WIDTH.config.getIntegerValue() / getScale());
    }

    public void render(MatrixStack matrixStack, int ticks, float partialTicks) {
       if (ConfigStorage.ChatScreen.VISIBILITY.config.getOptionListValue() == ConfigStorage.Visibility.FOCUSONLY) {
            return;
        }

        int lineCount = getLineCount();

        boolean chatFocused = ConfigStorage.ChatScreen.VISIBILITY.config.getOptionListValue() == ConfigStorage.Visibility.ALWAYS || AdvancedChat.getAdvancedChatHud().isChatFocused();

        if (scrolledLines > lineCount) {
            scrolledLines = lineCount;
        }

        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.scalef((float) getScale(), (float) getScale(), 1);

        int lines = 0;
        int renderedLines = 0;
        int leftX = getLeftX();
        int padLX = getPaddedLeftX();
        int rightX = getRightX();
        int padRX = getPaddedRightX();
        LimitedInteger y = new LimitedInteger(getScaledHeight() - ConfigStorage.ChatScreen.TOP_PAD.config.getIntegerValue(), ConfigStorage.ChatScreen.BOTTOM_PAD.config.getIntegerValue());

        for (int j = 0; j < messages.size(); j++) {
            AdvancedChatMessage message = messages.get(j)  ;
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
                AdvancedChatMessage.AdvancedChatLine line = message.getLines().get(i);
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
        if (chatFocused) {
            drawRect(leftX, getActualY(y.getValue()), rightX, getActualY(getScaledHeight()), ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.getSimpleColor().color());
            // Scroll bar
            float add = (float) (scrolledLines) / (lineCount + 1);
            int scrollHeight = (int) (add * getScaledHeight());
            RenderUtils.drawRect(getScaledWidth() + leftX, getActualY(scrollHeight + 10), 1, 10, ColorUtil.WHITE.color());
        }

        RenderSystem.popMatrix();
    }

    private void drawLine(MatrixStack matrixStack, AdvancedChatMessage.AdvancedChatLine line, int x, int y, int pLX, int pRX, int lineIndex, int messageIndex, int renderedLines, boolean focused, int ticks) {
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
            background = ConfigStorage.ChatScreen.HUD_BACKGROUND_COLOR.config.getSimpleColor();
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
            int fadeStart = ConfigStorage.ChatScreen.FADE_START.config.getIntegerValue();
            int fadeStop = fadeStart + ConfigStorage.ChatScreen.FADE_TIME.config.getIntegerValue();
            int timeAlive = ticks - line.getParent().getCreationTick();
            float percent = (float) Math.min(1, (double) (timeAlive - fadeStart) / (double) (fadeStop - fadeStart));
            applied = 1 - (float) ((EasingMethod) ConfigStorage.ChatScreen.FADE_TYPE.config.getOptionListValue()).apply(percent);
            if (applied <= 0) {
                return;
            }
            if (applied < 1) {
                background = ColorUtil.fade(background, applied);
                text = ColorUtil.fade(text, applied);
            }
        }
        RenderUtils.drawRect(x, getActualY(y), getScaledWidth(), height, background.color());
        if (lineIndex == line.getParent().getLineCount() - 1 && line.getParent().getOwner() != null && ConfigStorage.General.CHAT_HEADS.config.getBooleanValue()) {
            RenderUtils.color(1, 1, 1, applied);
            client.getTextureManager().bindTexture(line.getParent().getOwner().getTexture());
            DrawableHelper.drawTexture(matrixStack, pLX - 10, getActualY(y), 8, 8, 8, 8, 8, 8, 64, 64);
            RenderUtils.color(1, 1, 1, 1);
        }

        Text render = line.getText();
        if (line.getParent().getStacks() > 0 && lineIndex == 0) {
            SplitText toPrint = new SplitText(render);
            Style style = Style.EMPTY;
            TextColor color = TextColor.fromRgb(ColorUtil.GRAY.color());
            style = style.withColor(color);
            toPrint.getSiblings().add(new SimpleText(" (" + line.getParent().getStacks() + ")", style));
            render = toPrint.getText();
        }

        DrawableHelper.drawTextWithShadow(matrixStack, client.textRenderer, render, pLX, getActualY(y) + 1, text.color());
    }

    public Style getText(double mouseX, double mouseY) {
        if (!AdvancedChat.getAdvancedChatHud().isChatFocused() || this.client.options.hudHidden) {
            return null;
        }
        double relX = mouseX;
        double relY = (double)this.client.getWindow().getScaledHeight() - mouseY - ConfigStorage.ChatScreen.Y.config.getIntegerValue();
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
        int lineCount = getLineCount();
        LimitedInteger y = new LimitedInteger(getScaledHeight(), ConfigStorage.ChatScreen.BOTTOM_PAD.config.getIntegerValue());
        for (AdvancedChatMessage message : messages) {
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
                    AdvancedChatMessage.AdvancedChatLine line = message.getLines().get(i);
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

}
