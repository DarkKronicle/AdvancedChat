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
import lombok.Setter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 * Base filter class that provides easy way to create new filters.
 */
public abstract class AbstractFilter {

    /**
     * String to be found in filter.
     */
    @Setter @Getter
    protected String filterString;

    /**
     * The {@link net.darkkronicle.advancedchat.storage.Filter.FindType} on how text will be filtered.
     */
    @Setter @Getter
    protected Filter.FindType findType;

    public AbstractFilter() {
        this.filterString = "";
    }

    /**
     * filterSring and findType
     * @param filterString What it searches to find.
     * @param findType How it finds it.
     */
    public AbstractFilter(String filterString, Filter.FindType findType) {
        this.filterString = filterString;
        this.findType = findType;
    }

    /**
     * Called whenever text needed to be filtered comes through. It gets returned
     * in an {@link Optional}. If the optional is empty then it will use the original filtered text.
     * If the optional contains something it will override the old text.
     * @param text StringRenderable to be filtered.
     * @return An {@link Optional} that if not empty will override the filtered text.
     */
    public abstract Optional<Text> filter(Text text);

}
