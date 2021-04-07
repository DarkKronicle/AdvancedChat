package io.github.darkkronicle.advancedchat.chat.tabs;

import io.github.darkkronicle.advancedchat.chat.ChatMessage;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.chat.ChatLogMessage;
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

    public MainChatTab() {
        super("Main", "Main");
        setUpTabs();
    }

    @Override
    public boolean shouldAdd(Text text) {
        return true;
    }

    @Override
    public void addMessage(ChatMessage line) {

        boolean forward = true;
        ArrayList<AbstractChatTab> added = new ArrayList<>();
        if (customChatTabs.size() > 0) {
            for (CustomChatTab tab : customChatTabs) {
                if (!tab.shouldAdd(line.getOriginalText())) {
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
        AdvancedChat.getChatLogData().addMessage(new ChatLogMessage(line, added.toArray(new AbstractChatTab[0])));
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
            CustomChatTab customTab = new CustomChatTab(tab.getName().config.getStringValue(), tab.getAbbreviation().config.getStringValue(), tab.getFind(), tab.getFindString().config.getStringValue(), tab.getForward().config.getBooleanValue(), tab.getStartingMessage().config.getStringValue());
            customChatTabs.add(customTab);
            allChatTabs.add(customTab);
        }
        for (ChatLogMessage l : AdvancedChat.getChatLogData().getMessages()) {
            for (AbstractChatTab t : customChatTabs) {
                t.addMessage(l);
            }
        }
    }

}
