package net.darkkronicle.advancedchat;

import net.darkkronicle.advancedchat.config.ConfigManager;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class AdvancedChat implements ClientModInitializer {
    public static ConfigManager configManager;
    public static ConfigStorage configStorage;
    public static MainFilter filter;

    @Override
    public void onInitializeClient() {
        configManager = new ConfigManager();
        Filter.checkForErrors(configStorage.filters);
        filter = new MainFilter();
    }
}
