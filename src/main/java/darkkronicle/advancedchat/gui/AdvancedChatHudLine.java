package darkkronicle.advancedchat.gui;

import darkkronicle.advancedchat.filters.FilteredMessage;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.time.LocalTime;

public class AdvancedChatHudLine extends ChatHudLine {

    private FilteredMessage.FilterResult[] type;
    private int repeats;
    private Text text;
    private int creationTick;
    private int id;
    private LocalTime time;


    public AdvancedChatHudLine(int creationTick, Text text, int id, FilteredMessage.FilterResult... type) {
        super(creationTick, text, id);
        this.text = text;
        this.creationTick = creationTick;
        this.id = id;
        this.type = type;
        repeats = 1;
        time = LocalTime.now();
    }

    public AdvancedChatHudLine(int creationTick, Text text, int id, int repeats, FilteredMessage.FilterResult... type) {
        super(creationTick, text, id);
        this.type = type;
        this.repeats = repeats;
        this.text = text;
        this.creationTick = creationTick;
        this.id = id;
        time = LocalTime.now();
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int num) {
        repeats = num;
    }

    public void addRepeat(int num) {
        repeats = repeats + num;
    }

    public void setText(Text text) {

    }

    public FilteredMessage.FilterResult[] getType() {
        return type;
    }

    public Text getText() {
        return this.text;
    }

    public int getCreationTick() {
        return this.creationTick;
    }

    public int getId() {
        return this.id;
    }

    public boolean doesInclude(FilteredMessage.FilterResult test) {
        for (FilteredMessage.FilterResult filter : this.type) {
            if (test == filter) {
                return true;
            }
        }
        return false;
    }

    public LocalTime getTime() {
        return time;
    }

}
