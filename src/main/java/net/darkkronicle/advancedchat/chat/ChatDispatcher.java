package net.darkkronicle.advancedchat.chat;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.MessageOwner;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.StyleFormatter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.time.LocalTime;
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
        Optional<Text> filtered = AdvancedChat.filter.filter(text);
        if (filtered.isPresent()) {
            text = filtered.get();
        }

        if (text.getString().length() <= 0) {
            return;
        }
        ColorUtil.SimpleColor backcolor;
        for (ColorFilter colorFilter : AdvancedChat.filter.getColorFilters()) {
            backcolor = colorFilter.getBackgroundColor(text);
            if (backcolor != null) {
                break;
            }
        }
        MessageOwner player = SearchUtils.getAuthor(client.getNetworkHandler(), unfiltered);
        AdvancedChat.chatTab.addMessage(text, 0, client.inGameHud.getTicks(), LocalTime.now(), player);
    }
}
