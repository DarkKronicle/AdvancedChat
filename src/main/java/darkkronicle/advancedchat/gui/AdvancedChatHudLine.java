package darkkronicle.advancedchat.gui;

import darkkronicle.advancedchat.filters.FilteredMessage;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

public class AdvancedChatHudLine extends ChatHudLine {

    private FilteredMessage.FilterResult type;

    public AdvancedChatHudLine(int creationTick, Text text, int id) {
        super(creationTick, text, id);
        type = FilteredMessage.FilterResult.UNKNOWN;
    }

    public AdvancedChatHudLine(int creationTick, Text text, int id, FilteredMessage.FilterResult type) {
        super(creationTick, text, id);
        this.type = type;
    }

    public FilteredMessage.FilterResult getType() {
        return type;
    }

}
