package net.darkkronicle.advancedchat.chat.tabs;

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.chat.AdvancedChatMessage;
import net.darkkronicle.advancedchat.config.ChatTab;
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

    public MainChatTab() {
        super("Main", "Main");
        setUpTabs();
    }

    @Override
    public boolean shouldAdd(Text text) {
        return true;
    }

    @Override
    public void addMessage(AdvancedChatMessage line) {

        boolean forward = true;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (customChatTabs.size() > 0) {
            for (CustomChatTab tab : customChatTabs) {
                if (!tab.shouldAdd(line.getRawText())) {
                    continue;
                }
                tab.addMessage(line);
                added.add(tab);
                if (!tab.isForward()) {
                    forward = false;
                    break;
                }
            }
        }
        if (forward) {
            added.add(this);
        }
        AdvancedChat.getChatLogData().addMessage(line.getRawText(), added.toArray(new AbstractChatTab[0]));
        if (!forward) {
            return;
        }

        super.addMessage(line);

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
