package net.darkkronicle.advancedchat.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@AllArgsConstructor
public class Filter {

    @Getter @Setter
    private String name;
    @Getter @Setter
    private boolean active;
    @Getter @Setter
    private String findString;
    @Getter @Setter
    private FindType findType;
    @Getter @Setter
    private ReplaceType replaceType;
    @Getter @Setter
    private SimpleText replaceTo;
    @Getter @Setter
    private NotifyType notifyType;

    public static final Filter EMPTY = new Filter("Default", false, "Cool", FindType.LITERAL, ReplaceType.ONLYMATCH, new SimpleText("AWESOME!", Style.EMPTY), NotifyType.NONE);

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
