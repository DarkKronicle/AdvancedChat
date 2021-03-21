package net.darkkronicle.advancedchat.storage;

import lombok.Data;


@Data
public class ChatTab {
    private String name;

    private String findString;

    private Filter.FindType findType;

    private String startingMessage;

    private boolean forward;

    private String abreviation;

    public ChatTab(String name, String findString, Filter.FindType findType, String startingMessage, boolean forward, String abreviation) {
        this.name = name;
        this.findString = findString;
        this.findType = findType;
        this.startingMessage = startingMessage;
        this.forward = forward;
        this.abreviation = abreviation;
    }
}
