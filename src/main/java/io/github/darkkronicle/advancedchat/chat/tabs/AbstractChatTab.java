package io.github.darkkronicle.advancedchat.chat.tabs;

import lombok.Data;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.*;

/**
 * Base ChatTab that allows for custom chat tabs in AdvancedChatHud.
 */
@Environment(EnvType.CLIENT)
@Data
public abstract class AbstractChatTab {

    // Each tab stores their own messages.
    protected String name;
    protected String abreviation;
    protected ColorUtil.SimpleColor mainColor;
    protected ColorUtil.SimpleColor borderColor;
    protected ColorUtil.SimpleColor innerColor;
    private int unread = 0;
    protected boolean showUnread;

    public AbstractChatTab(String name, String abreviation, ColorUtil.SimpleColor mainColor, ColorUtil.SimpleColor borderColor, ColorUtil.SimpleColor innerColor, boolean showUnread) {
        this.name = name;
        this.abreviation = abreviation;
        this.mainColor = mainColor;
        this.showUnread = showUnread;
        this.innerColor = innerColor;
        this.borderColor = borderColor;
    }

    public void addNewUnread() {
        this.unread++;
    }

    public void resetUnread() {
        this.unread = 0;
    }


    /**
     * If the inputted message should be put into the chat tab.
     *
     * @param text Object to search.
     * @return True if it should be added.
     */
    public abstract boolean shouldAdd(Text text);

}
