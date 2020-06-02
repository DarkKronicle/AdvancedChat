package darkkronicle.advancedchat.config;

import darkkronicle.advancedchat.AdvancedChatClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClothConfigScreen {
    public Screen getConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setTransparentBackground(true);
        builder.setTitle("AdvancedChat Options");
        ConfigCategory general = builder.getOrCreateCategory("General");
        ConfigEntryBuilder entry = builder.entryBuilder();

        general.addEntry(entry.startIntField("Stored Lines", AdvancedChatClient.configObject.storedLines).setDefaultValue(1000).setErrorSupplier(num -> {
            if (num < AdvancedChatClient.configObject.visibleLines) {
                return Optional.of("Stored lines can't be less than visible lines!");
            }
            return Optional.empty();
        }).build());

        general.addEntry(entry.startIntField("Visible Lines", AdvancedChatClient.configObject.visibleLines).setDefaultValue(100).setSaveConsumer(newval -> AdvancedChatClient.configObject.visibleLines = newval).setMax(500).setMin(50).build());

        general.addEntry(entry.startBooleanToggle("Log Bottom to Top", AdvancedChatClient.configObject.linesUpDown).setSaveConsumer(newval -> AdvancedChatClient.configObject.linesUpDown = newval).build());

        general.addEntry(entry.startBooleanToggle("Stack Messages", AdvancedChatClient.configObject.stackSame).setSaveConsumer(newval -> AdvancedChatClient.configObject.stackSame = newval).build());

        general.addEntry(entry.startBooleanToggle("Show Time in Log", AdvancedChatClient.configObject.showTime).setSaveConsumer(newval -> AdvancedChatClient.configObject.showTime = newval).build());

        general.addEntry(entry.startStrField("Time Format", AdvancedChatClient.configObject.timeFormat).setSaveConsumer(newval -> AdvancedChatClient.configObject.timeFormat = newval).setErrorSupplier(string -> {
            try {
                DateTimeFormatter.ofPattern(string);
                return Optional.empty();
            } catch (Exception e) {
                return Optional.of("Format isn't correct! (hh:mm:ss...)");
            }
        }).build());

        general.addEntry(entry.startStrField("Time Full Format", AdvancedChatClient.configObject.replaceFormat).setSaveConsumer(newval -> AdvancedChatClient.configObject.replaceFormat = newval).setErrorSupplier(string -> {
            if (string.contains("%TIME")) {
                return Optional.empty();
            }
            return Optional.of("Needs to include %TIME%");
        }).build());

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
