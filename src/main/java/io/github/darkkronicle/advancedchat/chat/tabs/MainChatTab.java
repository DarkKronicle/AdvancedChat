package io.github.darkkronicle.advancedchat.chat.tabs;

import io.github.darkkronicle.advancedchat.chat.ChatHistory;
import io.github.darkkronicle.advancedchat.chat.ChatMessage;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.config.ChatTab;
import net.minecraft.text.*;

import java.util.ArrayList;

/**
 * Main chat tab that manages other chat tabs.
 */
public class MainChatTab extends AbstractChatTab {
    @Getter
    private ArrayList<AbstractChatTab> allChatTabs = new ArrayList<>();
    @Getter
    private ArrayList<CustomChatTab> customChatTabs = new ArrayList<>();

    public static boolean nextSend = false;

    public MainChatTab() {
        super("Main", ConfigStorage.MainTab.ABBREVIATION.config.getStringValue(), ConfigStorage.MainTab.MAIN_COLOR.config.getSimpleColor(), ConfigStorage.MainTab.BORDER_COLOR.config.getSimpleColor(), ConfigStorage.MainTab.INNER_COLOR.config.getSimpleColor(), ConfigStorage.MainTab.SHOW_UNREAD.config.getBooleanValue());
        setUpTabs();
    }

    public void refreshOptions() {
        this.abreviation = ConfigStorage.MainTab.ABBREVIATION.config.getStringValue();
        this.mainColor = ConfigStorage.MainTab.MAIN_COLOR.config.getSimpleColor();
        this.innerColor = ConfigStorage.MainTab.INNER_COLOR.config.getSimpleColor();
        this.borderColor = ConfigStorage.MainTab.BORDER_COLOR.config.getSimpleColor();
        this.showUnread = ConfigStorage.MainTab.SHOW_UNREAD.config.getBooleanValue();
    }


    @Override
    public boolean shouldAdd(Text text) {
        return true;
    }

    /**
     * Method used for loading in tabs from the config.
     */
    public void setUpTabs() {
        customChatTabs = new ArrayList<>();
        allChatTabs = new ArrayList<>();
        allChatTabs.add(this);
        for (ChatTab tab : ConfigStorage.TABS) {
            CustomChatTab customTab = new CustomChatTab(tab);
            customChatTabs.add(customTab);
            allChatTabs.add(customTab);
        }
        for (ChatMessage message : ChatHistory.getInstance().getMessages()) {
            ArrayList<AbstractChatTab> tabs = new ArrayList<>();
            for (AbstractChatTab t : allChatTabs) {
                if (t.shouldAdd(message.getOriginalText())) {
                    tabs.add(t);
                }
            }
            message.setTabs(tabs);
        }
    }

}
