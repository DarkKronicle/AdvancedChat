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

import lombok.NonNull;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.util.Optional;

/**
 * Filter used to change the background color of a message.
 */
@Environment(EnvType.CLIENT)
public class ColorFilter extends AbstractFilter {
    /**
     * {@link net.darkkronicle.advancedchat.util.ColorUtil.SimpleColor} that will change the background color.
     */
    private final ColorUtil.SimpleColor color;

    public ColorFilter(String filterString, Filter.FindType findType, @NonNull ColorUtil.SimpleColor color) {
        super(filterString, findType);
        this.color = color;
    }

    // Doesn't change anything in text. Only happens after it goes through other filters.
    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        return Optional.empty();
    }

    // if returned null it won't do anything, but if not null then it will have the default color.
    // Probably not perfect to use null, may come back later.
    public ColorUtil.SimpleColor getBackgroundColor(StringRenderable text) {
        SplitText splitText = new SplitText(text);
        if (SearchText.isMatch(splitText.getFullMessage(), filterString, findType)) {
            return color;
        }
        return null;
    }
}
