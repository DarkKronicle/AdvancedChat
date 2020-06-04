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

package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class NotifyFilter {

    public FilteredMessage filter(String message, ConfigFilter filter) {
        if (!filter.isActive()) {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
        }
        if (!filter.isRegex()) {
            if (filter.isIgnoreCase()) {
                if (message.toLowerCase().contains(filter.getTrigger().toLowerCase())) {
                    if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                        notifyPlayer();
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                    } else {
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                    }
                }
            } else {
                if (message.contains(filter.getTrigger())) {
                    if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                        notifyPlayer();
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                    } else {
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                    }
                }
            }
        } else {
            Pattern pattern = Pattern.compile(filter.getTrigger());
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                    notifyPlayer();
                    return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                } else {
                    return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                }
            }

        }
        return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
    }

    public void notifyPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1));
    }
}
