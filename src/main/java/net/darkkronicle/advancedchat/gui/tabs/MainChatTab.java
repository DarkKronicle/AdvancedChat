package net.darkkronicle.advancedchat.gui.tabs;

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatLine;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * <h1>MainChatTab</h1>
 * Main chat tab that manages other chat tabs.
 */
public class MainChatTab extends AbstractChatTab {
    @Getter
    private ArrayList<AbstractChatTab> allChatTabs = new ArrayList<>();
    @Getter
    private ArrayList<CustomChatTab> customChatTabs = new ArrayList<>();

    public MainChatTab() {
        super("Main");
        setUpTabs();
    }

    @Override
    public boolean shouldAdd(StringRenderable stringRenderable) {
        return true;
    }

    @Override
    public void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl, LocalTime time) {
        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();
        MinecraftClient client = MinecraftClient.getInstance();
        StringRenderable unfiltered = stringRenderable;
        Optional<StringRenderable> filtered = AdvancedChat.filter.filter(stringRenderable);
        if (filtered.isPresent()) {
            stringRenderable = filtered.get();
        }

        ColorUtil.SimpleColor backcolor = null;
        for (ColorFilter colorFilter : AdvancedChat.filter.getColorFilters()) {
            backcolor = colorFilter.getBackgroundColor(stringRenderable);
        }
        // Goes through chat tabs
        boolean dontforward = false;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (customChatTabs.size() > 0) {
            for (CustomChatTab tab : customChatTabs) {
                if (tab.shouldAdd(stringRenderable)) {
                    tab.addMessage(stringRenderable, messageId, timestamp, bl);
                    added.add(tab);
                    if (!tab.isForward()) {
                        dontforward = true;
                        break;
                    }
                }
            }
        }
        if (!dontforward) {
            added.add(this);
        }
        AdvancedChat.getChatLogData().addMessage(unfiltered, added.toArray(new AbstractChatTab[0]));
        if (dontforward) {
            return;
        }
        if (!shouldAdd(stringRenderable)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }


        StringRenderable logged = stringRenderable;
        boolean showtime = AdvancedChat.configStorage.chatLogConfig.showTime;
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(AdvancedChat.configStorage.timeFormat);
            SplitText text = new SplitText(stringRenderable);
            text.addTime(format, time);
            stringRenderable = text.getStringRenderable();
        }
        int width = MathHelper.floor((double)hud.getWidth() / hud.getChatScale());
        List<StringRenderable> list = ChatMessages.breakRenderedChatMessageLines(stringRenderable, width, client.textRenderer);

        StringRenderable stringRenderable2;
        for(Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, new AdvancedChatLine(timestamp, stringRenderable2, messageId, time, backcolor))) {
            stringRenderable2 = (StringRenderable)var8.next();
            hud.messageAddedToTab(this);
        }

        int visibleMessagesMaxSize = AdvancedChat.configStorage.chatConfig.storedLines;
        while(this.visibleMessages.size() > visibleMessagesMaxSize) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!bl) {
            this.messages.add(0, new AdvancedChatLine(timestamp, logged, messageId, time, backcolor));

            while(this.messages.size() > visibleMessagesMaxSize) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    /**
     * <h1>setUpTabs</h1>
     * Method used for loading in tabs from the config.
     */
    public void setUpTabs() {
        customChatTabs = new ArrayList<>();
        allChatTabs = new ArrayList<>();
        allChatTabs.add(this);
        for (ChatTab tab : AdvancedChat.configStorage.tabs) {
            CustomChatTab customTab = new CustomChatTab(tab.getName(), tab.getFindType(), tab.getFindString(), tab.isForward(), tab.getStartingMessage());
            customChatTabs.add(customTab);
            allChatTabs.add(customTab);
        }
    }

}
