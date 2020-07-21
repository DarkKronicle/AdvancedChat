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

package net.darkkronicle.advancedchat.filters;

import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class NotifyFilter extends AbstractFilter {

    private Filter.NotifyType notifyType;

    public NotifyFilter(String toFind, Filter.FindType findType, Filter.NotifyType notifyType) {
        this.notifyType = notifyType;
        super.filterString = toFind;
        super.findType = findType;
    }

    @Override
    public Optional<Text> filter(Text text) {
        if (notifyType == Filter.NotifyType.SOUND) {
            if (SearchText.isMatch(text.getString(), filterString, findType)) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1));
            }
        }
        return Optional.empty();
    }
}
