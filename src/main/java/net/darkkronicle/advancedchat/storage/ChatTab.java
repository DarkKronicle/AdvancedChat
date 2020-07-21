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

package net.darkkronicle.advancedchat.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@AllArgsConstructor
@Data
public class ChatTab {
    @With
    private String name;

    private String findString;

    private Filter.FindType findType;

    private String startingMessage;

    private boolean forward;

    public final static ChatTab DEFAULT = new ChatTab("Default", "Name", Filter.FindType.LITERAL,  "", true);

    public static ChatTab getNewTab() {
        ChatTab toadd = ChatTab.DEFAULT;
        boolean changed = true;
        int i = 0;
        while (changed && i < 20) {
            changed = false;
            i++;
            for (ChatTab tab : AdvancedChat.configStorage.tabs) {
                if (toadd.getName().equals(tab.getName())) {
                    toadd.withName(toadd.getName() + "1");
                    changed = true;
                }
            }
        }
        return toadd;
    }

}
