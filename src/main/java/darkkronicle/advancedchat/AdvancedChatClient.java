package darkkronicle.advancedchat;

import darkkronicle.advancedchat.config.ConfigFilter;
import darkkronicle.advancedchat.config.ConfigManager;
import darkkronicle.advancedchat.config.ConfigObject;
import darkkronicle.advancedchat.filters.FilteredMessage;
import darkkronicle.advancedchat.filters.MainFilter;
import darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AdvancedChatClient implements ClientModInitializer {
    public static MainFilter mainFilter;
    private static long ticks;
    public static AdvancedChatHud chatHud;

    public static ConfigObject configObject;
    public static ConfigManager configManager;

    public static AdvancedChatHud getChatHud() {
        if (chatHud == null) {
            chatHud = new AdvancedChatHud(MinecraftClient.getInstance());
        }
        return chatHud;
    }

    public static void renderChatHud(float v) {
        if (chatHud == null) {
            chatHud = new AdvancedChatHud(MinecraftClient.getInstance());
        }
        chatHud.render((int)ticks);
    }

    @Override
    public void onInitializeClient() {
        ClientTickCallback.EVENT.register((client) -> {
            ticks++;
        });
        HudRenderCallback.EVENT.register(AdvancedChatClient::renderChatHud);
        configManager = new ConfigManager();
        if (configObject.configFilters == null) {
            configObject.configFilters = new ArrayList<>();
            configObject.configFilters.add(new ConfigFilter());
        }
        mainFilter = new MainFilter();
    }
}