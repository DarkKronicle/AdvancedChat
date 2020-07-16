package net.darkkronicle.advancedchat.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChatTab {
    private String name;

    private String findString;

    private Filter.FindType findType;

    private String startingMessage;

    private boolean forward;

    public final static ChatTab DEFAULT = new ChatTab("Default", "Name", Filter.FindType.LITERAL,  "", true);


}
