package net.darkkronicle.advancedchat.filters.processors;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.AdvancedChatMessage;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.chat.MessageOwner;
import net.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Environment(EnvType.CLIENT)
public class ChatTabProcessor implements IMessageProcessor {

    @Override
    public boolean process(Text text, Text unfiltered) {
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
        Text original = text;
        if (showtime) {
            SplitText split = new SplitText(text);
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.General.TIME_FORMAT.config.getStringValue());
            split.addTime(format, time);
            text = split.getText();
        }

        int width = 0;
        // Find player
        MessageOwner player = SearchUtils.getAuthor(MinecraftClient.getInstance().getNetworkHandler(), unfiltered);
        AdvancedChatMessage line = AdvancedChatMessage.builder()
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

}
