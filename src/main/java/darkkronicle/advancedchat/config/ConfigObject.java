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

package darkkronicle.advancedchat.config;

import java.util.List;

public class ConfigObject {
    public List<ConfigFilter> configFilters;
    public int storedLines = 1000;
    public int visibleLines = 100;
    public boolean stackSame = false;
    public boolean linesUpDown = true;
    public boolean clearChat = true;
    public boolean showTime = false;
    public String timeFormat = "hh:mm";
    public String replaceFormat = "&7[%TIME%] ";
}
