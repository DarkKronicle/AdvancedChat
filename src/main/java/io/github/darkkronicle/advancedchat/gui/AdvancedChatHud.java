package io.github.darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.interfaces.IRenderer;
import io.github.darkkronicle.advancedchat.chat.ChatMessage;
import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;

import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AdvancedChatHud implements IRenderer {
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private LinkedList<ChatWindow> windows = new LinkedList<>();
    private int dragX = 0;
    private int dragY = 0;
    private ChatWindow drag = null;
    private boolean resize = false;

    private AdvancedChatHud() {
        client = MinecraftClient.getInstance();
    }

    public void reset() {
        windows.clear();
        ChatWindow base = new ChatWindow(AdvancedChat.chatTab);
        base.setSelected(true);
        windows.add(base);
    }

    private static final AdvancedChatHud INSTANCE = new AdvancedChatHud();

    public static AdvancedChatHud getInstance() {
        return INSTANCE;
    }

    @Override
    public void onRenderGameOverlayPost(float partialTicks, MatrixStack matrixStack) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            windows.get(i).render(matrixStack, client.inGameHud.getTicks(), isChatFocused());
        }
    }

    public void resetScroll() {
        for (ChatWindow w : windows) {
            w.resetScroll();
        }
    }

    public void scroll(double amount) {
        for (ChatWindow w : windows) {
            if (w.isSelected()) {
                w.scroll(amount);
            }
        }
    }

    public void scroll(double amount, double mouseX, double mouseY) {
        for (ChatWindow w : windows) {
            if (w.isSelected() || w.isMouseOver(mouseX, mouseY)) {
                w.scroll(amount);
            }
        }
    }

    public Style getText(double mouseX, double mouseY) {
        for (ChatWindow w : windows) {
            if (w.isMouseOver(mouseX, mouseY)) {
                return w.getText(mouseX, mouseY);
            }
        }
        return null;
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof AdvancedChatScreen;
    }

    public ChatWindow getSelected() {
        for (ChatWindow w : windows) {
            if (w.isSelected()) {
                return w;
            }
        }
        return null;
    }

    public void setSelected(ChatWindow window) {
        for (ChatWindow w : windows) {
            w.setSelected(window.equals(w));
        }
        windows.remove(window);
        windows.addFirst(window);
    }


    public boolean mouseClicked(Screen screen, double mouseX, double mouseY, int button) {
        ChatWindow over = null;
        for (ChatWindow w : windows) {
            if (w.isMouseOver(mouseX, mouseY)) {
                over = w;
                break;
            }
        }
        if (over == null) {
            return false;
        }
        if (button == 0) {
            setSelected(over);
            if (over.isMouseOverDragBar(mouseX, mouseY)) {
                drag = over;
                dragX = (int) mouseX - over.getX();
                dragY = (int) mouseY - over.getY();
                resize = false;
            }
            Style style = over.getText(mouseX, mouseY);
            if (style != null && screen.handleTextClick(style)) {
                return true;
            }
            if (over.onMouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return true;
    }



    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (drag != null && !resize) {
            int x = Math.max((int) mouseX - dragX, 0);
            int y = Math.max((int) mouseY - dragY, drag.getActualHeight());
            x = Math.min(x, client.getWindow().getScaledWidth() - drag.getWidth());
            y = Math.min(y, client.getWindow().getScaledHeight());
            drag.setPosition(x, y);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (drag != null) {
            drag = null;
            return true;
        }
        return false;
    }

    public void onTabButton(AbstractChatTab tab) {
        for (ChatWindow w : windows) {
            if (w.isSelected()) {
                w.setTab(tab);
            }
        }
    }

    public void onTabAddButton(AbstractChatTab tab) {
        ChatWindow window = new ChatWindow(tab);
        ChatWindow sel = getSelected();
        if (sel == null) {
            sel = window;
        }
        window.setPosition(sel.getX() + 15, sel.getY() + 15);
        windows.add(window);
        setSelected(window);;
    }

    public void deleteWindow(ChatWindow chatWindow) {
        windows.remove(chatWindow);
        if (!windows.isEmpty()) {
            for (ChatWindow w : windows) {
                w.setSelected(false);
            }
            windows.get(0).setSelected(true);
        }
    }

    public void onNewMessage(ChatMessage message) {
        for (ChatWindow w : windows) {
            w.addMessage(message);
        }
    }

    public void clear() {
        messageHistory.clear();
    }
}
