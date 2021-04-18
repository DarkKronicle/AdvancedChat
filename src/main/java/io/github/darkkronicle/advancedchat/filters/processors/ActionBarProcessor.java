package io.github.darkkronicle.advancedchat.filters.processors;

import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionBarProcessor implements IMatchProcessor {

    @Override
    public boolean processMatches(FluidText text, FluidText unfiltered, SearchResult matches) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return true;
        }
        client.inGameHud.addChatMessage(MessageType.GAME_INFO, text, client.player.getUuid());
        return true;
    }

}
