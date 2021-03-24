package net.darkkronicle.advancedchat.gui.tabs;

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatMessage;
import net.darkkronicle.advancedchat.storage.ChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.darkkronicle.advancedchat.util.VisitingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main chat tab that manages other chat tabs.
 */
public class MainChatTab extends AbstractChatTab {
    @Getter
    private ArrayList<AbstractChatTab> allChatTabs = new ArrayList<>();
    @Getter
    private ArrayList<CustomChatTab> customChatTabs = new ArrayList<>();

    public MainChatTab() {
        super("Main", "Main");
        setUpTabs();
    }

    @Override
    public boolean shouldAdd(Text text) {
        return true;
    }

    @Override
    public void addMessage(Text text, int messageId, int timestamp, LocalTime time, PlayerListEntry player) {
        AdvancedChatHud hud = AdvancedChat.getAdvancedChatHud();
        MinecraftClient client = MinecraftClient.getInstance();
        text = VisitingUtils.format(text);
        Text unfiltered = text;
        Optional<Text> filtered = AdvancedChat.filter.filter(text);
        if (filtered.isPresent()) {
            text = filtered.get();
        }

        if (text.getString().length() <= 0) {
            return;
        }
        ColorUtil.SimpleColor backcolor = null;
        for (ColorFilter colorFilter : AdvancedChat.filter.getColorFilters()) {
            backcolor = colorFilter.getBackgroundColor(text);
            if (backcolor != null) {
                break;
            }
        }
        // Goes through chat tabs
        if (player == null) {
            player = SearchText.getAuthor(client.getNetworkHandler(), unfiltered);
        }
        boolean dontforward = false;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (customChatTabs.size() > 0) {
            for (CustomChatTab tab : customChatTabs) {
                if (tab.shouldAdd(text)) {
                    tab.addMessage(text, messageId, timestamp, time, player);
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
        if (!shouldAdd(text)) {
            return;
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }


        // To Prevent small letters from being stuck right next to the tab border we subtract 5 here.
        int width = MathHelper.floor(AdvancedChatHud.getScaledWidth() - 5 );

        for (int i = 0; i < ConfigStorage.General.CHAT_STACK.config.getIntegerValue() && i < messages.size(); i++) {
            AdvancedChatMessage chatLine = messages.get(i);
            if (text.getString().equals(chatLine.getRawText().getString())) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                return;
            }
        }
        boolean showtime = ConfigStorage.ChatScreen.SHOW_TIME.config.getBooleanValue();
        Text original = text;
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.General.TIME_FORMAT.config.getStringValue());
            SplitText split = new SplitText(text);
            split.addTime(format, time);
            text = split.getText();
        }

        AdvancedChatMessage line = AdvancedChatMessage.builder().originalText(original).text(text).owner(player).id(messageId).width(width).creationTick(timestamp).time(time).build();

        this.messages.add(0, line);

        hud.messageAddedToTab(this);

        int visibleMessagesMaxSize = ConfigStorage.ChatScreen.STORED_LINES.config.getIntegerValue();
        while(this.messages.size() > visibleMessagesMaxSize) {
            this.messages.remove(this.messages.size() - 1);
        }

    }

    public static List<MutableText> wrapText(TextRenderer textRenderer, int scaledWidth, Text text) {
        ArrayList<MutableText> lines = new ArrayList<>();
        for (OrderedText breakRenderedChatMessageLine : ChatMessages.breakRenderedChatMessageLines(text, scaledWidth, textRenderer)) {
            MutableText newLine = new LiteralText("");

            AtomicReference<Style> oldStyle = new AtomicReference<>(null);
            AtomicReference<String> s = new AtomicReference<>("");

            breakRenderedChatMessageLine.accept((index, style, codePoint) -> {
                if (oldStyle.get() == null) {
                    oldStyle.set(style);
                }

                if (oldStyle.get() != style) {
                    newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
                    oldStyle.set(style);
                    s.set("");
                }

                s.set(s.get() + (char) codePoint);
                return true;
            });

            if (!s.get().isEmpty()) {
                newLine.append(new LiteralText(s.get()).setStyle(oldStyle.get()));
            }
            lines.add(newLine);
        }
        return lines;
    }

    /**
     * Method used for loading in tabs from the config.
     */
    public void setUpTabs() {
        customChatTabs = new ArrayList<>();
        allChatTabs = new ArrayList<>();
        allChatTabs.add(this);
        for (ChatTab tab : ConfigStorage.TABS) {
            CustomChatTab customTab = new CustomChatTab(tab.getName(), tab.getAbreviation(), tab.getFindType(), tab.getFindString(), tab.isForward(), tab.getStartingMessage());
            customChatTabs.add(customTab);
            allChatTabs.add(customTab);
        }
    }

}
