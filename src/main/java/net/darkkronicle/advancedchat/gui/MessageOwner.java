package net.darkkronicle.advancedchat.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

@Data
@Value
@AllArgsConstructor
public class MessageOwner {
    String name;
    Identifier texture;
    PlayerListEntry entry;


}
