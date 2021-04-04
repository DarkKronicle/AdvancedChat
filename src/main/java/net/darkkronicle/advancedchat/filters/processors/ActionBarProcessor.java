package net.darkkronicle.advancedchat.filters.processors;

import net.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import net.darkkronicle.advancedchat.util.FluidText;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionBarProcessor implements IMatchProcessor {

    @Override
    public boolean processMatches(FluidText text, FluidText unfiltered, List<SearchUtils.StringMatch> matches) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return true;
        }
        client.inGameHud.addChatMessage(MessageType.GAME_INFO, text, client.player.getUuid());
        return true;
    }

}
