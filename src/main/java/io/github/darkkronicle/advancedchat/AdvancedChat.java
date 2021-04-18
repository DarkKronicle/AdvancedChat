package io.github.darkkronicle.advancedchat;

import fi.dy.masa.malilib.event.InitializationHandler;
import io.github.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import io.github.darkkronicle.advancedchat.config.ChatLogData;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import io.github.darkkronicle.advancedchat.gui.AdvancedSleepingChatScreen;
import io.github.darkkronicle.advancedchat.gui.ChatLogScreen;
import io.github.darkkronicle.advancedchat.util.SyncTaskQueue;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class AdvancedChat implements ClientModInitializer {
    public static MainChatTab chatTab;
    private static ChatLogData chatLogData;

    public static final String MOD_ID = "advancedchat";

    private final static Random RANDOM = new Random();

    private final static String[] RANDOM_STRINGS = {"yes", "maybe", "no", "potentially", "hello", "goodbye", "tail", "pop", "water", "headphone", "head", "scissor", "paper", "burger", "clock", "peg", "speaker", "computer", "mouse", "mat", "keyboard", "soda", "mac", "cheese", "home", "pillow", "couch", "drums", "drumstick", "math", "Euler", "Chronos", "DarkKronicle", "Kron", "pain", "suffer", "bridge", "Annevdl", "MaLiLib", "pog", "music", "pants", "glockenspiel", "marimba", "chimes", "vibraphone", "vibe", "snare", "monkeymode", "shades", "cactus", "shaker", "pit", "band", "percussion", "foot", "leg", "Kurt", "bruh", "gamer", "gaming"};

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
            SyncTaskQueue.getInstance().update(s.inGameHud.getTicks());
            if (keyBinding.wasPressed()) {
                s.openScreen(new ChatLogScreen());
            }
            if (client.currentScreen instanceof AdvancedSleepingChatScreen && !client.player.isSleeping()) {
                client.openScreen(null);
            }
        });
        File english = new File("./config/advancedchat/english.zip");
        if (!english.exists()) {
            new File("./config/advancedchat/").mkdirs();
            // Move dictionary so that we can access it easier
            try (FileOutputStream output = new FileOutputStream(english)){
                InputStream stream = getResource("english.zip");
                IOUtils.copy(stream, output);
                stream.close();
                System.out.println("Moved english jar!");
            } catch (Exception e) {
                System.out.println("Couldn't load english.jar");
                e.printStackTrace();
            }

        }
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



    public static InputStream getResource(String path) throws URISyntaxException, IOException {
        URI uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        if(uri.getScheme().contains("jar")){
            // Not IDE
            //jar.toString() begins with file:
            //i want to trim it out...
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        } else{
            // IDE
            return new FileInputStream(Paths.get(uri).toFile());

        }
    }

    public static String getRandomString() {
        return RANDOM_STRINGS[RANDOM.nextInt(RANDOM_STRINGS.length)];
    }

}
