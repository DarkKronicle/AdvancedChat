package net.darkkronicle.advancedchat.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class FilterScreen {

    // Screen for configuring filters.
    public static Screen getScreen(Screen parentScreen) {
        Random random = new Random();
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new Identifier(ModMenuImpl.TEXTURES[random.nextInt(ModMenuImpl.TEXTURES.length)]));
        builder.setSavingRunnable(ModMenuImpl::save);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        String[] select = {"1", "2"};

        // Goes through each filter that is saved. Saves it to a /storage/Filter.class
        for (Filter filter : AdvancedChat.configStorage.filters) {

            ConfigCategory category;
            if (builder.hasCategory(new LiteralText(filter.getName()))) {
                // If there is a name conflict, it renames it to the name + 1;
                filter.setName(filter.getName()+"1");
                ModMenuImpl.save();
            }

            category = builder.getOrCreateCategory(new LiteralText(filter.getName()));


            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.filter.name"), filter.getName()).setTooltip(new TranslatableText("config.advancedchat.filter.info.name")).setSaveConsumer(filter::setName).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.active"), filter.isActive()).setTooltip(new TranslatableText("config.advancedchat.filter.info.active")).setSaveConsumer(filter::setActive).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.findstring"), filter.getFindString()).setTooltip(new TranslatableText("config.advancedchat.info.findstring")).setSaveConsumer(filter::setFindString).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.findtype"), Filter.FindType.values(), filter.getFindType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.findtype.literal"),
                    new TranslatableText("config.advancedchat.info.findtype.regex"),
                    new TranslatableText("config.advancedchat.info.findtype.upperlower")
            ).setSaveConsumer(filter::setFindType).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.replacetype"), Filter.ReplaceType.values(), filter.getReplaceType()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacetype.none"),
                    new TranslatableText("config.advancedchat.filter.info.replacetype.onlymatch"),
                    new TranslatableText("config.advancedchat.filter.info.replacetype.fullline")
            ).setSaveConsumer(filter::setReplaceType).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.filter.replaceto"), filter.getReplaceTo().getMessage()).setTooltip(new TranslatableText("config.advancedchat.filter.info.replaceto")).setSaveConsumer(val -> filter.getReplaceTo().setMessage(val)).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.notifytype"), Filter.NotifyType.values(), filter.getNotifyType()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.notifytype.none"),
                    new TranslatableText("config.advancedchat.filter.info.notifytype.sound")
            ).setSaveConsumer(filter::setNotifyType).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.replacetextcolor"), filter.isReplaceTextColor()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacetextcolor")
            ).setSaveConsumer(filter::setReplaceTextColor).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.filter.replacebackground"), filter.isReplaceBackgroundColor()).setTooltip(
                    new TranslatableText("config.advancedchat.filter.info.replacebackground")
            ).setSaveConsumer(filter::setReplaceBackgroundColor).build());

            category.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.filter.color"), filter.getColor().color()).setSaveConsumer(newval -> {
                filter.setColor(new ColorUtil.SimpleColor(newval));
            }).setTooltip(new TranslatableText("config.advancedchat.filter.info.color")).build());



            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.filter.delete"), select, select[0]).setNameProvider((s -> {
                if (s.equalsIgnoreCase("1")) {
                    return new TranslatableText("config.advancedchat.click");
                }
                AdvancedChat.configStorage.filters.remove(filter);
                ModMenuImpl.save();
                MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
                return new TranslatableText("config.advancedchat.click");
            })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        }

        return builder.build();
    }


}
