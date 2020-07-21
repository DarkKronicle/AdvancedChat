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

import lombok.Data;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Data
public class AdvancedChatLine {
    private int creationTick;
    private StringRenderable text;
    private int id;
    private LocalTime time;
    private ColorUtil.SimpleColor background;
    private int stacks;
    private UUID uuid;

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, LocalTime localTime) {
        this(creationTick, text, id, localTime, null, 0);
    }

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, LocalTime localTime, ColorUtil.SimpleColor background, int stacks) {
        this(creationTick, text, id, localTime, background, stacks, UUID.randomUUID());
    }

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, LocalTime time, ColorUtil.SimpleColor background, int stacks, UUID uuid) {
        this.creationTick = creationTick;
        this.text = text;
        this.id = id;
        this.time = time;
        this.background = background;
        this.stacks = stacks;
        this.uuid = uuid;
    }

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, LocalTime time, UUID uuid) {
        this(creationTick, text, id, time, null, 0, uuid);
    }
}
