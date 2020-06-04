/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

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

    public int getRepeats() {
        return repeats;
    }

    public void addRepeat(int num) {
        repeats = repeats + num;
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
