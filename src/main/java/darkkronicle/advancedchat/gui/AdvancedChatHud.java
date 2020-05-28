package darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class AdvancedChatHud extends ChatHud {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;

    public AdvancedChatHud(MinecraftClient client) {
        super(client);
        this.client = client;
    }

    public void render(int ticks) {
        if (this.optionVisible()) {
            int visibleLineCount = this.getVisibleLineCount();
            int visibleMessagesCount = this.visibleMessages.size();
            if (visibleMessagesCount > 0) {
                boolean chatFocused = false;
                if (this.isChatFocused()) {
                    chatFocused = true;
                }

                double d = this.getChatScale();
                int k = MathHelper.ceil((double)this.getWidth() / d);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, client.getWindow().getScaledHeight()-40, 0.0F);
                RenderSystem.scaled(d, d, 1.0D);
                double textOpacity = this.client.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
                double backgroundOpacity = this.client.options.textBackgroundOpacity;
                int line = 0;
                Matrix4f matrix4f = Matrix4f.translate(0.0F, 0.0F, -100.0F);

                int ticksAlive;
                int fadeTextOpacity;
                int fadeBackgroundOpacity;
                for(int i = 0; i + this.scrolledLines < this.visibleMessages.size() && i < visibleLineCount; ++i) {
                    ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(i + this.scrolledLines);
                    if (chatHudLine != null) {
                        ticksAlive = ticks - chatHudLine.getCreationTick();
                        if (ticksAlive < 200 || chatFocused) {
                            double fade = chatFocused ? 1.0D : getMessageOpacityMultiplier(ticksAlive);
                            fadeTextOpacity = (int)(255.0D * fade * textOpacity);
                            fadeBackgroundOpacity = (int)(255.0D * fade * backgroundOpacity);
                            ++line;
                            if (fadeTextOpacity > 3) {
                                int y = (-i * 9);
                                fill(matrix4f, -2, y - 9, k + 4, y, fadeBackgroundOpacity << 24);
                                String string = chatHudLine.getText().asFormattedString();
                                RenderSystem.enableBlend();
                                this.client.textRenderer.drawWithShadow(string, 0.0F, (float)(y - 8), 16777215 + (fadeTextOpacity << 24));
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                            }
                        }
                    }
                }

                if (chatFocused) {
                    int textHeight = 9;
                    RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
                    int height = visibleMessagesCount * textHeight + visibleMessagesCount;
                    ticksAlive = line * textHeight + line;
                    int v = this.scrolledLines * ticksAlive / visibleMessagesCount;
                    int w = ticksAlive * ticksAlive / height;
                    if (height != ticksAlive) {
                        fadeTextOpacity = v > 0 ? 170 : 96;
                        fadeBackgroundOpacity = this.hasUnreadNewMessages ? 13382451 : 3355562;
                        fill(0, -v, 2, -v - w, fadeBackgroundOpacity + (fadeTextOpacity << 24));
                        fill(2, -v, 1, -v - w, 13421772 + (fadeTextOpacity << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

    private boolean optionVisible() {
        return this.client.options.chatVisibility != ChatVisibility.HIDDEN;
    }

    private static double getMessageOpacityMultiplier(int age) {
        double d = (double)age / 200.0D;
        d = 1.0D - d;
        d *= 10.0D;
        d = MathHelper.clamp(d, 0.0D, 1.0D);
        d *= d;
        return d;
    }

    public void clear(boolean clearHistory) {
        this.visibleMessages.clear();
        this.messages.clear();
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

    private void addMessage(Text message, int messageId, int timestamp, boolean bl) {
        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<Text> list = Texts.wrapLines(message, i, this.client.textRenderer, false, false);
        boolean bl2 = this.isChatFocused();

        Text text;
        for(Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, new ChatHudLine(timestamp, text, messageId))) {
            text = (Text)var8.next();
            if (bl2 && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1.0D);
            }
        }

        while(this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!bl) {
            this.messages.add(0, new ChatHudLine(timestamp, message, messageId));

            while(this.messages.size() > 1000) {
                this.messages.remove(this.messages.size() - 1);
            }
        }

    }

    public void reset() {
        this.visibleMessages.clear();
        this.resetScroll();

        for(int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine chatHudLine = (ChatHudLine)this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
        }

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
        this.hasUnreadNewMessages = false;
    }

    public void scroll(double amount) {
        this.scrolledLines = (int)((double)this.scrolledLines + amount);
        int i = this.visibleMessages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.hasUnreadNewMessages = false;
        }

    }

    public Text getText(double x, double y) {
        if (this.isChatFocused() && !this.client.options.hudHidden && this.optionVisible()) {
            double d = this.getChatScale();
            double e = x - 2.0D;
            double f = (double)this.client.getWindow().getScaledHeight() - y - 40.0D;
            e = (double)MathHelper.floor(e / d);
            f = (double)MathHelper.floor(f / d);
            if (e >= 0.0D && f >= 0.0D) {
                int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
                if (e <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale())) {
                    this.client.textRenderer.getClass();
                    if (f < (double)(9 * i + i)) {
                        this.client.textRenderer.getClass();
                        int j = (int)(f / 9.0D + (double)this.scrolledLines);
                        if (j >= 0 && j < this.visibleMessages.size()) {
                            ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(j);
                            int k = 0;
                            Iterator var15 = chatHudLine.getText().iterator();

                            while(var15.hasNext()) {
                                Text text = (Text)var15.next();
                                if (text instanceof LiteralText) {
                                    k += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false));
                                    if ((double)k > e) {
                                        return text;
                                    }
                                }
                            }
                        }

                        return null;
                    }
                }

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    public void removeMessage(int messageId) {
        Iterator iterator = this.visibleMessages.iterator();

        ChatHudLine chatHudLine2;
        while(iterator.hasNext()) {
            chatHudLine2 = (ChatHudLine)iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
            }
        }

        iterator = this.messages.iterator();

        while(iterator.hasNext()) {
            chatHudLine2 = (ChatHudLine)iterator.next();
            if (chatHudLine2.getId() == messageId) {
                iterator.remove();
                break;
            }
        }

    }

    public int getWidth() {
        return getWidth(this.client.options.chatWidth);
    }

    public int getHeight() {
        return getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
    }

    public double getChatScale() {
        return this.client.options.chatScale;
    }

    public static int getWidth(double widthOption) {
        return MathHelper.floor(widthOption * 280.0D + 40.0D);
    }

    public static int getHeight(double heightOption) {
        return MathHelper.floor(heightOption * 160.0D + 20.0D);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / 9;
    }

    public List<ChatHudLine> getMessages() {
        return messages;
    }
}
