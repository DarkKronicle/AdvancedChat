package darkkronicle.advancedchat;

import darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class AdvancedChatClient implements ClientModInitializer {

    private static long ticks;
    public static AdvancedChatHud chatHud;

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
    }
}