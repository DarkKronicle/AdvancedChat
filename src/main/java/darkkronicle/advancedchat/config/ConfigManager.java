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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File config;

    public ConfigManager() {
        config = new File(FabricLoader.getInstance().getConfigDirectory()+"/advancedchat/config.json");
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
