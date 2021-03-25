package net.darkkronicle.advancedchat.chat;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.MessageOwner;
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
import java.util.Optional;

/**
 * A hook into {@link MessageDispatcher} for handling events towards chat. (Chat Tabs, filters...)
 */
@Environment(EnvType.CLIENT)
public class ChatDispatcher implements IMessageProcessor {

    public ChatDispatcher() {

    }

    @Override
    public void process(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        Text unfiltered = text;

        // Filter text
        Optional<Text> filtered = AdvancedChat.filter.filter(text);
        if (filtered.isPresent()) {
            text = filtered.get();
        }
        if (text.getString().length() <= 0) {
            return;
        }
        // Grab the background color
        ColorUtil.SimpleColor backcolor = null;
        for (ColorFilter colorFilter : AdvancedChat.filter.getColorFilters()) {
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

        int width = MathHelper.floor(AdvancedChatHud.getScaledWidth() - 5);
        // Find player
        MessageOwner player = SearchUtils.getAuthor(client.getNetworkHandler(), unfiltered);
        AdvancedChatMessage line = AdvancedChatMessage.builder()
                .text(text)
                .originalText(original)
                .owner(player)
                .id(0)
                .width(width)
                .creationTick(client.inGameHud.getTicks())
                .time(time)
                .background(backcolor)
                .build();
        AdvancedChat.chatTab.addMessage(line);
    }
}
