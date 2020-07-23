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


@Environment(EnvType.CLIENT)
public class ChatTabScreen {
    public static Screen getScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create().
                setParentScreen(parentScreen);
        builder.setSavingRunnable(ModMenuImpl::save);
        ModMenuImpl.setBackground(builder);

        builder.alwaysShowTabs();
        ConfigEntryBuilder entry = builder.entryBuilder();

        String[] select = {"1", "2"};

        ConfigCategory chattabs = builder.getOrCreateCategory(new TranslatableText("config.advancedchat.category.chattabs"));

        chattabs.addEntry(entry.startIntField(new TranslatableText("config.advancedchat.chattab.storedlines"), AdvancedChat.configStorage.chatConfig.storedLines).setTooltip(new TranslatableText("config.advancedchat.chattab.info.storedlines")).setMin(50).setMax(1000).setSaveConsumer(newval -> {
            AdvancedChat.configStorage.chatConfig.storedLines = newval;
        }).setDefaultValue(200).build());

        chattabs.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.chattab.createnew"), select, select[0]).setNameProvider((s -> {
            if (s.equalsIgnoreCase("1")) {
                return new TranslatableText("config.advancedchat.click");
            }

            AdvancedChat.configStorage.tabs.add(ChatTab.getDefault());
            ModMenuImpl.save();
            MinecraftClient.getInstance().openScreen(ChatTabScreen.getScreen(parentScreen));
            return new TranslatableText("config.advancedchat.click");

        })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        for (ChatTab chatTab : AdvancedChat.configStorage.tabs) {

            ConfigCategory category;
            if (builder.hasCategory(new LiteralText(chatTab.getName())) || chatTab.getName().equalsIgnoreCase("Main")) {
                chatTab.setName(chatTab.getName()+"1");
                ModMenuImpl.save();
            }
            category = builder.getOrCreateCategory(new LiteralText(chatTab.getName()));


            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.chattab.name"), chatTab.getName()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.name")).setSaveConsumer(chatTab::setName).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.chattab.abreviation"), chatTab.getAbreviation()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.abreviation")).setSaveConsumer(chatTab::setAbreviation).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.findstring"), chatTab.getFindString()).setTooltip(new TranslatableText("config.advancedchat.info.findstring")).setSaveConsumer(chatTab::setFindString).build());

            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.findtype"), Filter.FindType.values(), chatTab.getFindType()).setTooltip(
                    new TranslatableText("config.advancedchat.info.findtype.literal"),
                    new TranslatableText("config.advancedchat.info.findtype.regex"),
                    new TranslatableText("config.advancedchat.info.findtype.upperlower")
            ).setSaveConsumer(chatTab::setFindType).build());

            category.addEntry(entry.startBooleanToggle(new TranslatableText("config.advancedchat.chattab.forward"), chatTab.isForward()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.forward")).setSaveConsumer(chatTab::setForward).build());

            category.addEntry(entry.startStrField(new TranslatableText("config.advancedchat.chattab.startingmessage"), chatTab.getStartingMessage()).setTooltip(new TranslatableText("config.advancedchat.chattab.info.startingmessage")).setSaveConsumer(chatTab::setStartingMessage).build());


            category.addEntry(entry.startSelector(new TranslatableText("config.advancedchat.chattab.delete"), select, select[0]).setNameProvider((s -> {
                if (s.equalsIgnoreCase("1")) {
                    return new TranslatableText("config.advancedchat.click");
                }
                AdvancedChat.configStorage.tabs.remove(chatTab);
                ModMenuImpl.save();
                MinecraftClient.getInstance().openScreen(getScreen(parentScreen));
                return new TranslatableText("config.advancedchat.click");
            })).setTooltip(new TranslatableText("warn.advancedchat.savefirst")).build());

        }

        return builder.build();
    }
}
