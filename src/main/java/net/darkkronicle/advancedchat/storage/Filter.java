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

package net.darkkronicle.advancedchat.storage;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.List;

/** Filter Storage
 * This class is used to store data for filters. Each filter is based off of this class. These are stored in an ArrayList for later usage.
 */

@Environment(EnvType.CLIENT)
@Data
@AllArgsConstructor
public class Filter {

    /**
     * Name is only cosmetic. Shows up when editing filters. Way to distinguish filters for the player.
      */
    private String name;

    /**
     * Whether or not it should be used to filter chat messages currently.
     */
    private boolean active;

    /**
     * The Expression to find a match. The way it is interpreted is defined by findType.
     */
    private String findString;

    /** How findString is used.
     * LITERAL checks just for a match character to character.
     * UPPERLOWER is like literal, but ignore case.
     * REGEX interprets findString as a regular expression.
     */
    private FindType findType;

    /**
     * How the found string is modified.
     * ONLYMATCH replaces only what was matched.
     * FULLLINE replaces the full message.
     */
    private ReplaceType replaceType;

    /**
     * What the found string replaces to. (ex. If replaceType is FULLLINE this will replace the message with this)
     */
    private String replaceTo;

    /** How the filter notifies the client of a found string.
     * SOUND plays a sound when the filter is triggered.
     */
    private NotifyType notifyType;

    private boolean replaceBackgroundColor;

    private ColorUtil.SimpleColor color;

    /**
     * The default filter. Used for new filters.
     */
    public static final Filter DEFAULT = new Filter("Default", false, "Cool", FindType.LITERAL, ReplaceType.ONLYMATCH,"AWESOME!", NotifyType.NONE,  false, ColorUtil.BLACK);

    /**
     * In case the config.json has a missing value this will prevent NPE's from happening when ever the filter is accessed.
     * @param filters Filters to input.
     */
    public static void checkForErrors(List<Filter> filters) {
        for (Filter filter : filters) {
            if (filter.name == null) {
                filter.name = "Default";
            }
            if (filter.findString == null) {
                filter.findString = "Cool";
            }
            if (filter.findType == null) {
                filter.findType = FindType.LITERAL;
            }
            if (filter.replaceType == null) {
                filter.replaceType = ReplaceType.NONE;
            }
            if (filter.replaceTo == null) {
                filter.replaceTo = "AWESOME!";
            }
            if (filter.notifyType == null) {
                filter.notifyType = NotifyType.NONE;
            }
            if (filter.color == null) {
                filter.color = ColorUtil.BLACK;
            }
        }
    }


    public enum FindType {
        @SerializedName("literal")
        LITERAL,
        @SerializedName("upperlower")
        UPPERLOWER,
        @SerializedName("regex")
        REGEX
    }

    public enum ReplaceType {
        @SerializedName("none")
        NONE,
        @SerializedName("onlymatch")
        ONLYMATCH,
        @SerializedName("fullmessage")
        FULLMESSAGE
    }

    public enum NotifyType {
        @SerializedName("none")
        NONE,
        @SerializedName("sound")
        SOUND
    }

}
