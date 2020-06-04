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

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MainFilter {

    private ReplaceFilter replaceFilter;
    private NotifyFilter notifyFilter;

    public MainFilter() {
        replaceFilter = new ReplaceFilter();
        notifyFilter = new NotifyFilter();
    }

    /*
    TODO
    Eventually have filters take in Text objects to retain click events and other
    Text specific things.
     */
    public FilteredMessage filter(String message) {
        boolean filtered = false;
        FilteredMessage result = null;
        List<FilteredMessage.FilterResult> filters = new ArrayList<>();

        for (ConfigFilter filt : AdvancedChatClient.configObject.configFilters) {
            FilteredMessage mess = null;
            if (filt.getReplaceType() != ConfigFilter.ReplaceType.NONE) {
                mess = replaceFilter.filter(message, filt);
                if (!mess.doesInclude(FilteredMessage.FilterResult.UNKNOWN)) {
                    result = mess;
                    filters.add(mess.getResult()[0]);
                }
                if (!filtered) {
                    filtered = mess.isFiltered();
                }
            }

            FilteredMessage notify;
            if (filt.getNotifyType() != ConfigFilter.NotifyType.NONE) {
                notify = notifyFilter.filter(message, filt);
                if (!notify.doesInclude(FilteredMessage.FilterResult.UNKNOWN)) {
                    filters.add(notify.getResult()[0]);
                }
                if (!filtered) {
                    filtered = notify.isFiltered();
                }
                if (result == null) {
                    result = new FilteredMessage(message, true, false);
                }
            }
        }

        if (filtered) {
            if (filters.size() != 0) {
                result.setResult(filters.toArray(new FilteredMessage.FilterResult[2]));
            }
            return result;
        }
        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.ALLOW);
    }
}
