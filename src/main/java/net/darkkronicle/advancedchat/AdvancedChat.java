package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedSleepingChatScreen;
import net.darkkronicle.advancedchat.gui.ChatLogScreen;
import net.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import net.darkkronicle.advancedchat.config.ChatLogData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AdvancedChat implements ClientModInitializer {
    public static MainFilter filter;
    public static MainChatTab chatTab;
    private static ChatLogData chatLogData;

    public static final String MOD_ID = "advancedchat";

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

        KeyBinding keyBinding = new KeyBinding(
                "advancedchat.key.openlog",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                "advancedchat.category.keys"
        );
        KeyBindingHelper.registerKeyBinding(keyBinding);
        MinecraftClient client = MinecraftClient.getInstance();
        ClientTickEvents.START_CLIENT_TICK.register(s -> {
            if (keyBinding.wasPressed()) {
                s.openScreen(new ChatLogScreen());
            }
            if (client.currentScreen instanceof AdvancedSleepingChatScreen && !client.player.isSleeping()) {
                client.openScreen(null);
            }
        });
    }

    public static AdvancedChatHud getAdvancedChatHud() {
        // TODO remove
        return AdvancedChatHud.getInstance();
    }

    public static ChatLogData getChatLogData() {
        if (chatLogData == null) {
            chatLogData = new ChatLogData();
        }
        return chatLogData;
    }

}
