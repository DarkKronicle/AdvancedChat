package net.darkkronicle.advancedchat.gui.tabs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.text.StringRenderable;

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


    public CustomChatTab(String name, Filter.FindType findType, String findString, boolean forward, String startingMessage) {
        super(name);
        this.name = name;
        this.findType = findType;
        this.findString = findString;
        this.forward = forward;
        this.startingMessage = startingMessage;
    }


    @Override
    public boolean shouldAdd(StringRenderable stringRenderable) {
        SplitText text = new SplitText(stringRenderable);
        return SearchText.isMatch(text.getFullMessage(), findString, findType);
    }
}
