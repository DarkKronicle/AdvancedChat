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

package darkkronicle.advancedchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import darkkronicle.advancedchat.AdvancedChatClient;
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
            AdvancedChatClient.configObject = new ConfigObject();
            saveConfig();
            return;
        }
        boolean failed = false;
        try {
            AdvancedChatClient.configObject = GSON.fromJson(new FileReader(config), ConfigObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            failed = true;
        }

        if (failed || AdvancedChatClient.configObject == null) {
            AdvancedChatClient.configObject = new ConfigObject();
        }
        saveConfig();
    }

    public void saveConfig() throws IOException {
        config.getParentFile().mkdirs();
        if (!config.exists() && !config.createNewFile()) {
            AdvancedChatClient.configObject = new ConfigObject();
            return;
        }
        try {
            String result = GSON.toJson(AdvancedChatClient.configObject);
            if (!config.exists()) {
                config.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(config, false);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            AdvancedChatClient.configObject = new ConfigObject();
        }
    }
}
