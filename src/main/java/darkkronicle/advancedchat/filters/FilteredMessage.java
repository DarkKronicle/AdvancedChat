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

public class FilteredMessage {
    private String message;
    private FilterResult[] result;
    private boolean filtered;
    private boolean showUnfiltered;

    public FilteredMessage(String message, boolean filtered, boolean showUnfiltered, FilterResult... result) {
        this.message = message;
        this.result = result;
        this.filtered = filtered;
        this.showUnfiltered = showUnfiltered;
    }

    public String getMessage() {
        return message;
    }

    public FilterResult[] getResult() {
        return result;
    }

    public boolean isShowUnfiltered() {
        return showUnfiltered;
    }

    public void setResult(FilterResult[] result) {
        this.result = result;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public boolean doesInclude(FilterResult test) {
        for (FilterResult filter : this.result) {
            if (test == filter) {
                return true;
            }
        }
        return false;
    }

    public enum FilterResult {
        REPLACE,
        BLOCK,
        NOTIFY,
        UNREAD,
        BANNER,
        UNKNOWN,
        ALLOW
    }
}
