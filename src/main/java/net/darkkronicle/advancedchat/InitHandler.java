package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        fi.dy.masa.malilib.config.ConfigManager.getInstance().registerConfigHandler(AdvancedChat.MOD_ID, new ConfigStorage());
        AdvancedChat.filter = new MainFilter();
        AdvancedChat.chatTab = new MainChatTab();
        AdvancedChat.getAdvancedChatHud();
    }

}
