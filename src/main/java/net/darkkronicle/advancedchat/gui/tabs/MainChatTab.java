package net.darkkronicle.advancedchat.gui.tabs;

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.MessageOwner;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.filters.ColorFilter;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.darkkronicle.advancedchat.gui.AdvancedChatMessage;
import net.darkkronicle.advancedchat.config.ChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.darkkronicle.advancedchat.util.StyleFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
    public void addMessage(Text text, int messageId, int timestamp, LocalTime time, MessageOwner player) {

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
        AdvancedChat.getChatLogData().addMessage(text, added.toArray(new AbstractChatTab[0]));
        if (dontforward) {
            return;
        }

        super.addMessage(text, messageId, timestamp, time, player);

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
