package io.github.darkkronicle.advancedchat.filters.processors;

import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.chat.ChatHistory;
import io.github.darkkronicle.advancedchat.chat.ChatLogMessage;
import io.github.darkkronicle.advancedchat.chat.ChatMessage;
import io.github.darkkronicle.advancedchat.chat.MessageOwner;
import io.github.darkkronicle.advancedchat.chat.tabs.AbstractChatTab;
import io.github.darkkronicle.advancedchat.chat.tabs.CustomChatTab;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ChatTabProcessor implements IMatchProcessor {

    public static boolean nextSend = false;

    @Override
    public boolean process(FluidText text, FluidText unfiltered) {
        // Grab the background color
        if (unfiltered == null) {
            unfiltered = text;
        }
        ColorUtil.SimpleColor backcolor = text.getBackgroundColor();

        // Put the time in
        LocalTime time = LocalTime.now();
        boolean showtime = ConfigStorage.ChatScreen.SHOW_TIME.config.getBooleanValue();
        // Store original so we can get stuff without the time
        Text original = text.copy();
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.General.TIME_FORMAT.config.getStringValue());
            text.addTime(format, time);
        }

        int width = 0;
        // Find player
        MessageOwner player = SearchUtils.getAuthor(MinecraftClient.getInstance().getNetworkHandler(), unfiltered.getString());
        ChatMessage line = ChatMessage.builder()
                .displayText(text)
                .originalText(original)
                .owner(player)
                .id(0)
                .width(width)
                .creationTick(MinecraftClient.getInstance().inGameHud.getTicks())
                .time(time)
                .background(backcolor)
                .build();
        addMessage(line);
        return true;
    }

    public void addMessage(ChatMessage line) {
        boolean forward = true;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (nextSend) {
            nextSend = false;
            AbstractChatTab defaultTo = AdvancedChatHud.getInstance().getSelected().getTab();
            if (defaultTo.equals(AdvancedChat.chatTab)) {
                forward = false;
            }
            added.add(defaultTo);
        }
        if (AdvancedChat.chatTab.getCustomChatTabs().size() > 0) {
            for (CustomChatTab tab : AdvancedChat.chatTab.getCustomChatTabs()) {
                if (!tab.shouldAdd(line.getOriginalText())) {
                    continue;
                }
                if (added.contains(tab)) {
                    continue;
                }
                added.add(tab);
                if (!tab.isForward()) {
                    forward = false;
                    break;
                }
            }
        }
        if (forward) {
            added.add(AdvancedChat.chatTab);
        }
        for (AbstractChatTab tab : added) {
            tab.addNewUnread();
        }
        line.setTabs(added);
        AdvancedChat.getChatLogData().addMessage(new ChatLogMessage(line, added.toArray(new AbstractChatTab[0])));
        ChatHistory.getInstance().add(line);
    }

    @Override
    public Result processMatches(FluidText text, FluidText unfiltered, SearchResult matches) {
        return Result.getFromBool(process(text, unfiltered));
    }

}
