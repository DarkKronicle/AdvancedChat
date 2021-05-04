package io.github.darkkronicle.advancedchat.chat.tabs;

import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
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
    @Getter
    private ChatTab storage;


    public CustomChatTab(ChatTab storage) {
        super(storage.getName().config.getStringValue(), storage.getAbbreviation().config.getStringValue(), storage.getMainColor().config.getSimpleColor(), storage.getBorderColor().config.getSimpleColor(), storage.getInnerColor().config.getSimpleColor(), storage.getShowUnread().config.getBooleanValue());
        this.storage = storage;
        this.findType = storage.getFind();
        this.findString = storage.getFindString().config.getStringValue();
        this.forward = storage.getForward().config.getBooleanValue();
        this.startingMessage = storage.getStartingMessage().config.getStringValue();
    }


    @Override
    public boolean shouldAdd(Text text) {
        FluidText newText = new FluidText(text);
        return SearchUtils.isMatch(newText.getString(), findString, findType);
    }
}
