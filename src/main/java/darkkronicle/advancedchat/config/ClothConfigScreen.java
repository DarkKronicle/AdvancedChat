package darkkronicle.advancedchat.config;

import darkkronicle.advancedchat.AdvancedChatClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class ClothConfigScreen {
    public Screen getConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setTransparentBackground(true);
        builder.setTitle("AdvancedChat Options");
        ConfigCategory general = builder.getOrCreateCategory("General");
        ConfigEntryBuilder entry = builder.entryBuilder();

        general.addEntry(entry.startIntField("Stored Lines", AdvancedChatClient.configObject.storedLines).setDefaultValue(1000).setSaveConsumer(newval -> AdvancedChatClient.configObject.storedLines = newval).setMax(5000).setMin(100).build());

        general.addEntry(entry.startIntField("Visible Lines", AdvancedChatClient.configObject.visibleLines).setDefaultValue(100).setSaveConsumer(newval -> AdvancedChatClient.configObject.visibleLines = newval).setMax(500).setMin(50).build());

        general.addEntry(entry.startBooleanToggle("Log Bottom to Top", AdvancedChatClient.configObject.linesUpDown).setSaveConsumer(newval -> AdvancedChatClient.configObject.linesUpDown = newval).build());

        general.addEntry(entry.startBooleanToggle("Stack Messages", AdvancedChatClient.configObject.stackSame).setSaveConsumer(newval -> AdvancedChatClient.configObject.stackSame = newval).build());

        builder.setSavingRunnable(() -> {
            try {
                AdvancedChatClient.configManager.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        return builder.build();
    }

}
