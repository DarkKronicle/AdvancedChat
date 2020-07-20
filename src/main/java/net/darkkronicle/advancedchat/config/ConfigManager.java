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

package net.darkkronicle.advancedchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class ConfigManager {
    // Based off of she daniel's code that was released under Apache License 2.0.
    // https://github.com/shedaniel/i-need-keybinds/blob/master/src/main/java/me/shedaniel/ink/ConfigManager.java
    // http://www.apache.org/licenses/LICENSE-2.0

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File config;

    public ConfigManager() {
        config = new File(FabricLoader.getInstance().getConfigDirectory() + "/advancedchat/filterconfig.json");
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() throws IOException {
        config.getParentFile().mkdirs();
        if (!config.exists() || !config.canRead()) {
            AdvancedChat.configStorage = new ConfigStorage();
//            if (AdvancedChat.configStorage.filters.size() == 0) {
//                AdvancedChat.configStorage.filters.add(Filter.DEFAULT);
//                try {
//                    saveConfig();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (AdvancedChat.configStorage.tabs.size() == 0) {
//                AdvancedChat.configStorage.tabs.add(ChatTab.DEFAULT);
//                try {
//                    saveConfig();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            saveConfig();
            return;
        }
        boolean failed = false;
        try {
            AdvancedChat.configStorage = GSON.fromJson(new FileReader(config), ConfigStorage.class);
        } catch (Exception e) {
            e.printStackTrace();
            failed = true;
        }

        if (failed || AdvancedChat.configStorage == null) {
            AdvancedChat.configStorage = new ConfigStorage();
        }
//        if (AdvancedChat.configStorage.filters.size() == 0) {
//            AdvancedChat.configStorage.filters.add(Filter.DEFAULT);
//            try {
//                saveConfig();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (AdvancedChat.configStorage.tabs.size() == 0) {
//            AdvancedChat.configStorage.tabs.add(ChatTab.DEFAULT);
//            try {
//                saveConfig();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        saveConfig();
    }

    public void saveConfig() throws IOException {
        config.getParentFile().mkdirs();
        if (!config.exists() && !config.createNewFile()) {
            AdvancedChat.configStorage = new ConfigStorage();
            return;
        }
        try {
            String result = GSON.toJson(AdvancedChat.configStorage);
            if (!config.exists()) {
                config.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(config, false);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            AdvancedChat.configStorage = new ConfigStorage();
        }
    }

}
