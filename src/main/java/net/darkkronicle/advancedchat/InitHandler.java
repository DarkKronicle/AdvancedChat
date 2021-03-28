package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.chat.MessageDispatcher;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import net.darkkronicle.advancedchat.filters.processors.ChatTabProcessor;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(AdvancedChat.MOD_ID, new ConfigStorage());
        RenderEventHandler.getInstance().registerGameOverlayRenderer(AdvancedChatHud.getInstance());
        AdvancedChat.filter = new MainFilter();
        AdvancedChat.chatTab = new MainChatTab();
        ChatDispatcher.getInstance().setFinalProcessor(new ChatTabProcessor());
        MessageDispatcher.getInstance().register(ChatDispatcher.getInstance(), -1);
        AdvancedChat.getAdvancedChatHud();
    }

}
