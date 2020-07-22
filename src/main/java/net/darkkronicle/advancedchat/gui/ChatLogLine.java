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

package net.darkkronicle.advancedchat.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ChatLogLine {
    private StringRenderable text;
    private int id;
    private AbstractChatTab[] tab;
    private LocalTime time = LocalTime.now();
    private UUID uuid;

    public ChatLogLine(StringRenderable text, int id, AbstractChatTab... tab) {
        this.tab = tab;
        this.id = id;
        this.text = text;
        this.uuid = UUID.randomUUID();
    }

    public ChatLogLine(StringRenderable text, int id, LocalTime time, UUID uuid, AbstractChatTab... tab) {
        this.tab = tab;
        this.id = id;
        this.text = text;
        this.uuid = uuid;
        this.time = time;
    }

    public ChatLogLine(StringRenderable text, int id, AbstractChatTab[] tab, LocalTime time) {
        this.text = text;
        this.tab = tab;
        this.id = id;
        this.time = time;
        this.uuid = UUID.randomUUID();
    }
}
