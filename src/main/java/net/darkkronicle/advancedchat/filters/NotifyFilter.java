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
import net.minecraft.text.Text;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class NotifyFilter extends AbstractFilter {

    private Filter.NotifySounds notifySound;
    private float volume;
    private float pitch;

    public NotifyFilter(String toFind, Filter.FindType findType, Filter.NotifySounds notifySound, float volume, float pitch) {
        this.notifySound = notifySound;
        super.filterString = toFind;
        super.findType = findType;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public Optional<Text> filter(Text text) {
        if (notifySound != Filter.NotifySounds.NONE) {
            if (SearchText.isMatch(text.getString(), filterString, findType)) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(notifySound.getEvent(), pitch, volume));
            }
        }
        return Optional.empty();
    }
}
