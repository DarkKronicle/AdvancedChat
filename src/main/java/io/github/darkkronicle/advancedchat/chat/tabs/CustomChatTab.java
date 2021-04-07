package io.github.darkkronicle.advancedchat.chat.tabs;

import io.github.darkkronicle.advancedchat.config.Filter;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.config.ChatTab;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.FluidText;
import net.minecraft.text.Text;

/**
 * ChatTab that loads from {@link ChatTab}.
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
        FluidText newText = new FluidText(text);
        return SearchUtils.isMatch(newText.getString(), findString, findType);
    }
}
