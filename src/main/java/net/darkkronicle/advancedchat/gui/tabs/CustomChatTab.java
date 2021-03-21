package net.darkkronicle.advancedchat.gui.tabs;

import lombok.Getter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.text.Text;

/**
 * ChatTab that loads from {@link net.darkkronicle.advancedchat.storage.ChatTab}.
 * Easy to customize.
 */
public class CustomChatTab extends AbstractChatTab {
    @Getter
    private String name;
    @Getter
    private Filter.FindType findType;
    @Getter
    private String findString;
    @Getter
    private boolean forward;
    @Getter
    private String startingMessage;


    public CustomChatTab(String name, String abreviation, Filter.FindType findType, String findString, boolean forward, String startingMessage) {
        super(name, abreviation);
        this.name = name;
        this.findType = findType;
        this.findString = findString;
        this.forward = forward;
        this.startingMessage = startingMessage;
    }


    @Override
    public boolean shouldAdd(Text text) {
        SplitText newText = new SplitText(text);
        return SearchText.isMatch(newText.getFullMessage(), findString, findType);
    }
}
