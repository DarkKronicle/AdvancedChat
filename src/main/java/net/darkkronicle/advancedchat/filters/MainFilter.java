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

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.util.ArrayList;
import java.util.Optional;

// Filter that handles all other filters.
@Environment(EnvType.CLIENT)
public class MainFilter extends AbstractFilter {

    @Getter
    private ArrayList<ColorFilter> colorFilters = new ArrayList<>();

    private ArrayList<AbstractFilter> filters = new ArrayList<>();

    public MainFilter() {
        loadFilters();
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        // Filters through all filters.
        StringRenderable modifiedtext = null;
        for (AbstractFilter filter : filters) {
            Optional<StringRenderable> newtext = filter.filter(text);
            if (newtext.isPresent()) {
                modifiedtext = newtext.get();
                text = modifiedtext;
            }
        }
        if (modifiedtext != null) {
            return Optional.of(modifiedtext);

        }
        return Optional.empty();
    }

    /**
     * Loads filters that are stored in ConfigStorage.
     * Converts {@link Filter} into an {@link AbstractFilter}
     */
    public void loadFilters() {
       filters = new ArrayList<>();
       colorFilters = new ArrayList<>();
       for (Filter filter : AdvancedChat.configStorage.filters) {
           // If it replaces anything.
           if (!filter.isActive()) {
               continue;
           }
           if (filter.getReplaceType() != Filter.ReplaceType.NONE) {
               if (filter.isReplaceTextColor()) {
                   filters.add(new ReplaceFilter(filter.getFindString(), filter.getReplaceTo().replaceAll("&", "§"), filter.getFindType(), filter.getReplaceType(), filter.getColor()));
               } else {
                   filters.add(new ReplaceFilter(filter.getFindString(), filter.getReplaceTo().replaceAll("&", "§"), filter.getFindType(), filter.getReplaceType(), null));
               }
           }
           if (filter.getNotifySound() != Filter.NotifySounds.NONE) {
               filters.add(new NotifyFilter(filter.getFindString(), filter.getFindType(), filter.getNotifySound(), filter.getSoundVol(), filter.getSoundPitch()));
           }
           if (filter.isReplaceBackgroundColor()) {
                colorFilters.add(new ColorFilter(filter.getFindString(), filter.getFindType(), filter.getColor()));
           }
       }
    }
}
