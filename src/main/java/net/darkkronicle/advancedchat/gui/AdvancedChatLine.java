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
import lombok.Value;
import lombok.experimental.NonFinal;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.time.LocalTime;

@Environment(EnvType.CLIENT)
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Value
public class AdvancedChatLine {
    int creationTick;
    @NonFinal
    StringRenderable text;
    int id;
    @NonFinal
    LocalTime time = LocalTime.now();
    @NonFinal
    ColorUtil.SimpleColor background;
    @NonFinal
    int stacks = 0;

    public AdvancedChatLine(int creationTick, StringRenderable text, int id, LocalTime localTime) {
        this.creationTick = creationTick;
        this.text = text;
        this.id = id;
        time = localTime;
    }



}
