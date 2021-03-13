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

import lombok.Builder;
import lombok.Data;
import net.darkkronicle.advancedchat.gui.tabs.MainChatTab;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Data
public class AdvancedChatMessage {
    private int creationTick;
    private Text text;
    private Text rawText;
    private int id;
    private LocalTime time;
    private ColorUtil.SimpleColor background;
    private int stacks;
    private UUID uuid;
    private PlayerListEntry owner;
    private ArrayList<AdvancedChatLine> lines;

    @Data
    public static class AdvancedChatLine {
        private Text text;
        private final AdvancedChatMessage parent;
        private int width;

        private AdvancedChatLine(AdvancedChatMessage parent, Text text) {
            this.parent = parent;
            this.text = text;
            this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
        }
    }

    @Builder
    private AdvancedChatMessage(int creationTick, Text text, Text originalText, int id, LocalTime time, ColorUtil.SimpleColor background, int width, PlayerListEntry owner) {
        this.creationTick = creationTick;
        this.text = text;
        this.id = id;
        this.time = time;
        this.background = background;
        this.stacks = 0;
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.rawText = originalText == null ? text : originalText;
        formatChildren(width);
    }

    public void formatChildren(int width) {
        this.lines = new ArrayList<>();
        if (width == 0) {
            this.lines.add(new AdvancedChatLine(this, text));
        } else {
            for (Text t : MainChatTab.wrapText(MinecraftClient.getInstance().textRenderer, width, text)) {
                this.lines.add(new AdvancedChatLine(this, t));
            }
        }
    }

    public int getLineCount() {
        return this.lines.size();
    }

}
