package net.darkkronicle.advancedchat.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class ModMenuImpl implements ModMenuApi {

    // Textures that are randomly selected for backgrounds.
    public static final String[] TEXTURES = {"minecraft:textures/block/cobblestone.png", "minecraft:textures/block/oak_planks.png", "minecraft:textures/block/blue_wool.png",
            "minecraft:textures/block/yellow_wool.png", "minecraft:textures/block/pink_concrete.png", "minecraft:textures/block/blue_concrete.png", "minecraft:textures/block/gray_terracotta.png"};


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuImpl::getScreen;
    }

    public static void save() {
        try {
            AdvancedChat.configManager.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AdvancedChat.filter.loadFilters();
        AdvancedChat.chatTab.setUpTabs();
    }

    public static Screen getScreen(Screen parent) {
        Random random = new Random();
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parent)
                .setDefaultBackgroundTexture(new Identifier(TEXTURES[random.nextInt(TEXTURES.length)]));
        builder.setSavingRunnable(ModMenuImpl::save);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.general"));
        general.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.timeformat"), AdvancedChat.configStorage.timeFormat).setTooltip(new TranslatableText("config.advancedchat.info.timeformat")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.timeFormat = newval;
        }).setErrorSupplier(string -> {
            try {
                DateTimeFormatter.ofPattern(string);
                return Optional.empty();
            } catch (Exception e) {
                return Optional.of(new TranslatableText("warn.advancedchat.timeformaterror"));
            }
        }).setDefaultValue("hh:mm").build());

        general.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.replaceformat"), AdvancedChat.configStorage.replaceFormat).setTooltip(new TranslatableText("config.advancedchat.info.replaceformat")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.replaceFormat = newval;
        }).setErrorSupplier(string -> {
            if (string.contains("%TIME%")) {
                return Optional.empty();
            }
            return Optional.of(new TranslatableText("warn.advancedchat.replaceformaterror"));
        }).setDefaultValue("[%TIME%] ").build());

        general.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.timecolor"), AdvancedChat.configStorage.timeColor.color()).setTooltip(new TranslatableText("config.advancedchat.info.timecolor")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.timeColor = ColorUtil.intToColor(newval);

        }).setDefaultValue(ColorUtil.WHITE.color()).build());

        // Filters category. Used for configuring general filter options as well as opening the FilterScreen and creating new.
        ConfigCategory filters = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.filters"));

        // select is used to detect when you push the button. Currently ClothConfig has no way of having a button execute custom code,
        // So this does the next best thing by checking what the current option is.
        String[] select = {"1", "2"};
        filters.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filtermenu"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }
            MinecraftClient.getInstance().openScreen(FilterScreen.getScreen(parent));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        filters.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.createnew"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }
            AdvancedChat.configStorage.filters.add(Filter.DEFAULT);
            save();
            MinecraftClient.getInstance().openScreen(FilterScreen.getScreen(parent));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());


        ConfigCategory chattabs = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chattabs"));

        chattabs.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.chattab.storedlines"), AdvancedChat.configStorage.chatConfig.storedLines).setTooltip(new TranslatableText("config.advancedchat.chattab.info.storedlines")).setMin(50).setMax(1000).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.storedLines = newval;
        }).setDefaultValue(200).build());

        chattabs.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.tabmenu"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }
            MinecraftClient.getInstance().openScreen(ChatTabScreen.getScreen(parent));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        chattabs.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.chattab.createnew"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }

            AdvancedChat.configStorage.tabs.add(ChatTab.DEFAULT);
            save();
            MinecraftClient.getInstance().openScreen(ChatTabScreen.getScreen(parent));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());


        ConfigCategory chathud = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chathud"));

        chathud.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.chathud.showtime"), AdvancedChat.configStorage.chatConfig.showTime).setTooltip(new TranslatableText("config.advancedchat.chathud.info.showtime")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.showTime = newval;
        }).setDefaultValue(false).build());

        chathud.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.chathud.backgroundcolor"), AdvancedChat.configStorage.chatConfig.hudBackground.color()).setTooltip(new TranslatableText("config.advancedchat.chathud.info.backgroundcolor")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.hudBackground = ColorUtil.intToColor(newval);

        }).setDefaultValue(ColorUtil.BLACK.withAlpha(100).color()).build());

        chathud.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.chathud.emptytextcolor"), AdvancedChat.configStorage.chatConfig.emptyText.color()).setTooltip(new TranslatableText("config.advancedchat.chathud.info.emptytextcolor")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.emptyText = ColorUtil.intToColor(newval);

        }).setDefaultValue(ColorUtil.WHITE.color()).build());

        ConfigCategory chatlog = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chatlog"));

        chatlog.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.chatlog.showtime"), AdvancedChat.configStorage.chatLogConfig.showTime).setTooltip(new TranslatableText("config.advancedchat.chatlog.info.showtime")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatLogConfig.showTime = newval;
        }).setDefaultValue(false).build());

        chatlog.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.chatlog.storedlines"), AdvancedChat.configStorage.chatLogConfig.storedLines).setTooltip(new TranslatableText("config.advancedchat.chatlog.info.storedlines")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatLogConfig.storedLines = newval;
        }).setMin(100).setMax(3000).setDefaultValue(1000).build());

        return builder.build();
    }
}
