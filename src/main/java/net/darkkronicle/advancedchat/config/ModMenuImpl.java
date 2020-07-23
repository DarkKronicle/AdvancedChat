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

    public static void setBackground(ConfigBuilder builder) {
        ConfigStorage.Background background = AdvancedChat.configStorage.background;
        if (background == ConfigStorage.Background.RANDOM) {
            Random random = new Random();
            builder.setDefaultBackgroundTexture(new Identifier(TEXTURES[random.nextInt(TEXTURES.length)]));
        } else if (background == ConfigStorage.Background.TRANSPARENT) {
            builder.setTransparentBackground(true);
        }
    }

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
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parent);
        builder.setSavingRunnable(ModMenuImpl::save);
        setBackground(builder);

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

        general.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.background"), ConfigStorage.Background.values(), AdvancedChat.configStorage.background).setTooltip(
                new TranslatableText("config.advancedchat.info.background"),
                new TranslatableText("config.advancedchat.info.background.random"),
                new TranslatableText("config.advancedchat.info.background.transparent"),
                new TranslatableText("config.advancedchat.info.background.vanilla")
        ).setDefaultValue(ConfigStorage.Background.RANDOM).setSaveConsumer(val -> AdvancedChat.configStorage.background = val).build());

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
            AdvancedChat.configStorage.filters.add(Filter.getDefault());
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

            AdvancedChat.configStorage.tabs.add(ChatTab.getDefault());
            save();
            MinecraftClient.getInstance().openScreen(ChatTabScreen.getScreen(parent));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());


        ConfigCategory chathud = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chathud"));

        chathud.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.chathud.showtime"), AdvancedChat.configStorage.chatConfig.showTime).setTooltip(new TranslatableText("config.advancedchat.chathud.info.showtime")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.showTime = newval;
        }).setDefaultValue(false).build());

        chathud.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.alternatelines"), AdvancedChat.configStorage.alternatelines).setTooltip(new TranslatableText("config.advancedchat.info.alternatelines")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.alternatelines = newval;
        }).setDefaultValue(false).build());

        chathud.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.visibility"), ConfigStorage.Visibility.values(), AdvancedChat.configStorage.visibility).setTooltip(
                new TranslatableText("config.advancedchat.info.visibility.vanilla"),
                new TranslatableText("config.advancedchat.info.visibility.always"),
                new TranslatableText("config.advancedchat.info.visibility.focusonly")
        ).setDefaultValue(ConfigStorage.Visibility.VANILLA).setSaveConsumer(vis -> {
            AdvancedChat.configStorage.visibility = vis;
        }).build());

        chathud.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.chathud.backgroundcolor"), AdvancedChat.configStorage.chatConfig.hudBackground.color()).setTooltip(new TranslatableText("config.advancedchat.chathud.info.backgroundcolor")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.hudBackground = ColorUtil.intToColor(newval);

        }).setDefaultValue(ColorUtil.BLACK.withAlpha(100).color()).build());

        chathud.addEntry(entry.startAlphaColorField(new TranslatableText("config.advancedchat.chathud.emptytextcolor"), AdvancedChat.configStorage.chatConfig.emptyText.color()).setTooltip(new TranslatableText("config.advancedchat.chathud.info.emptytextcolor")).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.emptyText = ColorUtil.intToColor(newval);

        }).setDefaultValue(ColorUtil.WHITE.color()).build());

        chathud.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.chatstack"), AdvancedChat.configStorage.chatStack).setTooltip(new TranslatableText("config.advancedchat.info.chatstack")).setDefaultValue(0).setMin(0).setMax(20).setSaveConsumer(val -> {
            AdvancedChat.configStorage.chatStack = val;
        }).build());

        chathud.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.xoffset"), AdvancedChat.configStorage.chatConfig.xOffset).setTooltip(new TranslatableText("config.advancedchat.info.xoffset")).setMin(0).setMax(500).setSaveConsumer(val -> {
            AdvancedChat.configStorage.chatConfig.xOffset = val;
        }).setDefaultValue(0).build());

        chathud.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.yoffset"), AdvancedChat.configStorage.chatConfig.yOffset).setTooltip(new TranslatableText("config.advancedchat.info.yoffset")).setMin(10).setMax(1000).setSaveConsumer(val -> {
            AdvancedChat.configStorage.chatConfig.yOffset = val;
        }).setDefaultValue(30).build());

        chathud.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.width"), AdvancedChat.configStorage.chatConfig.width).setTooltip(new TranslatableText("config.advancedchat.info.width")).setMin(100).setMax(1000).setSaveConsumer(val -> {
            AdvancedChat.configStorage.chatConfig.width = val;
        }).setDefaultValue(280).build());

        chathud.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.height"), AdvancedChat.configStorage.chatConfig.height).setTooltip(new TranslatableText("config.advancedchat.info.height")).setMin(100).setMax(700).setSaveConsumer(val -> {
            AdvancedChat.configStorage.chatConfig.height = val;
        }).setDefaultValue(171).build());

        chathud.addEntry(entry.startIntSlider(new TranslatableText("config.advancedchat.tabchar"), AdvancedChat.configStorage.chatConfig.sideChars, 1, 10).setTooltip(new TranslatableText("config.advancedchat.info.tabchar"), new TranslatableText("config.advancedchat.info.tabchar2")).setSaveConsumer(val -> AdvancedChat.configStorage.chatConfig.sideChars = val).build());

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
