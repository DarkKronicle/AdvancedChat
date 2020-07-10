package net.darkkronicle.advancedchat.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.List;

/** Filter Storage
 * This class is used to store data for filters. Each filter is based off of this class. These are stored in an ArrayList for later usage.
 */

@Environment(EnvType.CLIENT)
@AllArgsConstructor
public class Filter {

    /**
     * Name is only cosmetic. Shows up when editing filters. Way to distinguish filters for the player.
      */
    @Getter @Setter
    private String name;

    /**
     * Whether or not it should be used to filter chat messages currently.
     */
    @Getter @Setter
    private boolean active;

    /**
     * The Expression to find a match. The way it is interpreted is defined by findType.
     */
    @Getter @Setter
    private String findString;

    /** How findString is used.
     * LITERAL checks just for a match character to character.
     * UPPERLOWER is like literal, but ignore case.
     * REGEX interprets findString as a regular expression.
     */
    @Getter @Setter
    private FindType findType;

    /**
     * How the found string is modified.
     * ONLYMATCH replaces only what was matched.
     * FULLLINE replaces the full message.
     */
    @Getter @Setter
    private ReplaceType replaceType;

    /**
     * What the found string replaces to. (ex. If replaceType is FULLLINE this will replace the message with this)
     */
    @Getter @Setter
    private SimpleText replaceTo;

    /** How the filter notifies the client of a found string.
     * SOUND plays a sound when the filter is triggered.
     */
    @Getter @Setter
    private NotifyType notifyType;

    /** 
     * The default filter. Used for new filters.
     */
    public static final Filter DEFAULT = new Filter("Default", false, "Cool", FindType.LITERAL, ReplaceType.ONLYMATCH, new SimpleText("AWESOME!", Style.EMPTY), NotifyType.NONE);

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
                filter.replaceTo = new SimpleText("AWESOME!", Style.EMPTY);
            }
            if (filter.notifyType == null) {
                filter.notifyType = NotifyType.NONE;
            }
        }
    }


    public enum FindType {
        LITERAL,
        UPPERLOWER,
        REGEX
    }

    public enum ReplaceType {
        NONE,
        ONLYMATCH,
        FULLMESSAGE
    }

    public enum NotifyType {
        NONE,
        SOUND
    }

}
