package net.darkkronicle.advancedchat.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class FilterScreen {

    private static final String[] TEXTURES = {"minecraft:textures/block/cobblestone.png", "minecraft:textures/block/oak_planks.png", "minecraft:textures/block/blue_wool.png",
            "minecraft:textures/block/yellow_wool.png", "minecraft:textures/block/pink_concrete.png", "minecraft:textures/block/dried_kelp_top.png", "minecraft:textures/block/gray_terracotta.png"};

    public static Screen getScreen(Screen parentScreen) {
        Random random = new Random();
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new Identifier(TEXTURES[random.nextInt(TEXTURES.length)]));
        builder.setSavingRunnable(FilterScreen::save);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.general"));

        general.addEntry(entry.startBooleanToggle(new LiteralText("AAAH"), false).build());


        ConfigCategory filters = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.filters"));
        String[] select = {"1", "2"};
        filters.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.createnew"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }
            AdvancedChat.configStorage.filters.add(Filter.EMPTY);
            save();
            MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
            return new TranslatableText("config.advancedchat.click");
        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());


        for (Filter filter : AdvancedChat.configStorage.filters) {

            ConfigCategory category;
            if (builder.hasCategory(new LiteralText(filter.getName()))) {
                filter.setName(filter.getName()+"1");
            }
            category = builder.getOrCreateCategory(new LiteralText(filter.getName()));


            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.name"), filter.getName()).setTooltip(new TranslatableText("config.advancedchat.info.name")).setSaveConsumer(filter::setName).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.active"), filter.isActive()).setTooltip(new TranslatableText("config.advancedchat.info.active")).setSaveConsumer(filter::setActive).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.findstring"), filter.getFindString()).setTooltip(new TranslatableText("config.advancedchat.info.findstring")).setSaveConsumer(filter::setFindString).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.findtype"), Filter.FindType.values(), filter.getFindType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.findtype.literal"),
                    new TranslatableText("config.advancedchat.info.findtype.regex"),
                    new TranslatableText("config.advancedchat.info.findtype.upperlower")
            ).setSaveConsumer(filter::setFindType).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.replacetype"), Filter.ReplaceType.values(), filter.getReplaceType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.replacetype.none"),
                    new TranslatableText("config.advancedchat.info.replacetype.onlymatch"),
                    new TranslatableText("config.advancedchat.info.replacetype.fullline")
            ).setSaveConsumer(filter::setReplaceType).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.replaceto"), filter.getReplaceTo().getMessage()).setTooltip(new TranslatableText("config.advancedchat.info.replaceto")).setSaveConsumer(val -> filter.getReplaceTo().setMessage(val)).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.notifytype"), Filter.NotifyType.values(), filter.getNotifyType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.notifytype.none"),
                    new TranslatableText("config.advancedchat.info.notifytype.sound")
            ).setSaveConsumer(filter::setNotifyType).build());



            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.delete"), select, select[0]).setNameProvider((s -> {
                if (s.equalsIgnoreCase("1")) {
                    return new TranslatableText("config.advancedchat.click");
                }
                AdvancedChat.configStorage.filters.remove(filter);
                save();
                MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
                return new TranslatableText("config.advancedchat.click");
            })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        }

        return builder.build();
    }

    public static void save() {
        try {
            AdvancedChat.configManager.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
