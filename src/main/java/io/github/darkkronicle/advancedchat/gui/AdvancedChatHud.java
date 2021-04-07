package io.github.darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.interfaces.IRenderer;
import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import lombok.Getter;
import lombok.Setter;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AdvancedChatHud implements IRenderer {
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    @Getter
    @Setter
    private AbstractChatTab currentTab;

    private AdvancedChatHud() {
        client = MinecraftClient.getInstance();
        currentTab = AdvancedChat.chatTab;
    }

    private static final AdvancedChatHud INSTANCE = new AdvancedChatHud();

    public static AdvancedChatHud getInstance() {
        return INSTANCE;
    }

    @Override
    public void onRenderGameOverlayPost(float partialTicks, MatrixStack matrixStack) {
        if (currentTab == null) {
            currentTab = AdvancedChat.chatTab;
        }
        currentTab.render(matrixStack, client.inGameHud.getTicks(), partialTicks);
    }

    public void clear(boolean clearHistory) {
        for (AbstractChatTab tab : AdvancedChat.chatTab.getAllChatTabs()) {
            tab.messages.clear();
        }
        if (clearHistory) {
            this.messageHistory.clear();
        }

    }

    public void resetScroll() {
        for (AbstractChatTab t : AdvancedChat.chatTab.getAllChatTabs()) {
            t.resetScroll();
        }
    }

    public void scroll(double amount) {
        currentTab.scroll(amount);
    }


    public Style getText(double mouseX, double mouseY) {
        return currentTab.getText(mouseX, mouseY);
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof AdvancedChatScreen;
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
