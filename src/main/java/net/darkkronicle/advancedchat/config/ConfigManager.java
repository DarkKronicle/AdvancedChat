package net.darkkronicle.advancedchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
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
            if (AdvancedChat.configStorage.filters.size() == 0) {
                AdvancedChat.configStorage.filters.add(Filter.EMPTY);
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        if (AdvancedChat.configStorage.filters.size() == 0) {
            AdvancedChat.configStorage.filters.add(Filter.EMPTY);
            try {
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
