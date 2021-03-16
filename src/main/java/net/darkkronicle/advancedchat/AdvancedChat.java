/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedSleepingChatScreen;
import net.darkkronicle.advancedchat.gui.ChatLogScreen;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.darkkronicle.advancedchat.storage.ChatLogData;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
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
    private static AdvancedChatHud advancedChatHud;
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
        if (advancedChatHud == null) {
            advancedChatHud = new AdvancedChatHud(MinecraftClient.getInstance());
        }
        return advancedChatHud;
    }

    public static ChatLogData getChatLogData() {
        if (chatLogData == null) {
            chatLogData = new ChatLogData();
        }
        return chatLogData;
    }

}
