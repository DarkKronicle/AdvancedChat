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

import net.darkkronicle.advancedchat.config.ConfigManager;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.MainFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.ChatLogScreen;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.darkkronicle.advancedchat.storage.ChatLogData;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AdvancedChat implements ClientModInitializer {
    public static ConfigManager configManager;
    public static ConfigStorage configStorage;
    public static MainFilter filter;
    private static AdvancedChatHud advancedChatHud;
    public static MainChatTab chatTab;
    private static ChatLogData chatLogData;

    @Override
    public void onInitializeClient() {
        configManager = new ConfigManager();
        Filter.checkForErrors(configStorage.filters);
        ChatTab.checkForErrors(configStorage.tabs);
        filter = new MainFilter();
        getAdvancedChatHud();

        chatTab = new MainChatTab();

        FabricKeyBinding keyBinding = FabricKeyBinding.Builder.create(
                new Identifier("advancedchat", "openlog"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                "category.advancedchat.keys"
        ).build();
        KeyBindingRegistry.INSTANCE.register(keyBinding);
        ClientTickCallback.EVENT.register(s -> {
            if (keyBinding.wasPressed()) {
                s.openScreen(new ChatLogScreen());
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
