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

package darkkronicle.advancedchat;

import darkkronicle.advancedchat.config.ConfigFilter;
import darkkronicle.advancedchat.config.ConfigManager;
import darkkronicle.advancedchat.config.ConfigObject;
import darkkronicle.advancedchat.filters.MainFilter;
import darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

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