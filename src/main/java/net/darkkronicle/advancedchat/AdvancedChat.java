package net.darkkronicle.advancedchat;

import net.darkkronicle.advancedchat.config.ConfigManager;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class AdvancedChat implements ClientModInitializer {
    public static ConfigManager configManager;
    public static ConfigStorage configStorage;
    public static MainFilter filter;
    private static AdvancedChatHud advancedChatHud;
    public static MainChatTab chatTab;

    @Override
    public void onInitializeClient() {
        configManager = new ConfigManager();
        Filter.checkForErrors(configStorage.filters);
        filter = new MainFilter();
        getAdvancedChatHud();

        chatTab = new MainChatTab();
    }


    public static AdvancedChatHud getAdvancedChatHud() {
        if (advancedChatHud == null) {
            advancedChatHud = new AdvancedChatHud(MinecraftClient.getInstance());
        }
        return advancedChatHud;
    }

}
