package net.darkkronicle.advancedchat.filters.processors;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.ChatMessage;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.chat.MessageOwner;
import net.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChatTabProcessor implements IMatchProcessor {

    @Override
    public boolean process(FluidText text, FluidText unfiltered) {
        // Grab the background color
        if (unfiltered == null) {
            unfiltered = text;
        }
        ColorUtil.SimpleColor backcolor = null;
        for (ColorFilter colorFilter : ChatDispatcher.getInstance().getColorFilters()) {
            backcolor = colorFilter.getBackgroundColor(text);
            if (backcolor != null) {
                break;
            }
        }

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
        AdvancedChat.chatTab.addMessage(line);
        return true;
    }

    @Override
    public boolean processMatches(FluidText text, FluidText unfiltered, @Nullable List<SearchUtils.StringMatch> matches) {
        return process(text, unfiltered);
    }

}
