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

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ChatTabScreen {
    public static Screen getScreen(Screen parentScreen) {
        Random random = new Random();
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(new Identifier(ModMenuImpl.TEXTURES[random.nextInt(ModMenuImpl.TEXTURES.length)]));
        builder.setSavingRunnable(ModMenuImpl::save);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        String[] select = {"1", "2"};

        ConfigCategory chattabs = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chattabs").getString());

        chattabs.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.chattab.storedlines").getString(), AdvancedChat.configStorage.chatConfig.storedLines).setTooltip(new TranslatableText("config.advancedchat.chattab.info.storedlines").getString()).setMin(50).setMax(1000).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.storedLines = newval;
        }).setDefaultValue(200).build());

        chattabs.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.chattab.createnew").getString(), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click").getString();
            }

            AdvancedChat.configStorage.tabs.add(ChatTab.DEFAULT);
            ModMenuImpl.save();
            MinecraftClient.getInstance().openScreen(ChatTabScreen.getScreen(parentScreen));
            return new TranslatableText("config.advancedchat.click").getString();

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst").getString()).build());

        for (ChatTab chatTab : AdvancedChat.configStorage.tabs) {

            ConfigCategory category;
            if (builder.hasCategory(chatTab.getName()) || chatTab.getName().equalsIgnoreCase("Main")) {
                chatTab.setName(chatTab.getName()+"1");
                ModMenuImpl.save();
            }
            category = builder.getOrCreateCategory(chatTab.getName());


            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.chattab.name").getString(), chatTab.getName()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.name").getString()).setSaveConsumer(chatTab::setName).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.findstring").getString(), chatTab.getFindString()).setTooltip(new TranslatableText("config.advancedchat.info.findstring").getString()).setSaveConsumer(chatTab::setFindString).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.findtype").getString(), Filter.FindType.values(), chatTab.getFindType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.findtype.literal").getString(),
                    new TranslatableText("config.advancedchat.info.findtype.regex").getString(),
                    new TranslatableText("config.advancedchat.info.findtype.upperlower").getString()
            ).setSaveConsumer(chatTab::setFindType).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.chattab.forward").getString(), chatTab.isForward()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.forward").getString()).setSaveConsumer(chatTab::setForward).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.chattab.startingmessage").getString(), chatTab.getStartingMessage()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.startingmessage").getString()).setSaveConsumer(chatTab::setStartingMessage).build());


            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.chattab.delete").getString(), select, select[0]).setNameProvider((s -> {
                if (s.equalsIgnoreCase("1")) {
                    return new TranslatableText("config.advancedchat.click").getString();
                }
                AdvancedChat.configStorage.tabs.remove(chatTab);
                ModMenuImpl.save();
                MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
                return new TranslatableText("config.advancedchat.click").getString();
            })).setTooltip(new TranslatableText("warn.advancedchat.savefirst").getString()).build());

        }

        return builder.build();
    }
}
