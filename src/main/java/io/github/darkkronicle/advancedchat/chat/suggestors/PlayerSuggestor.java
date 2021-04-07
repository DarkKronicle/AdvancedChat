package io.github.darkkronicle.advancedchat.chat.suggestors;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PlayerSuggestor implements IMessageSuggestor {

    @Override
    public Optional<List<Suggestion>> suggestCurrentWord(String text, StringRange range) {
        List<Suggestion> newSuggestions = new ArrayList<>();
        Collection<String> names = MinecraftClient.getInstance().player.networkHandler.getCommandSource().getPlayerNames();
        for (String name : names) {
            if (text.equals("") || name.toLowerCase().startsWith(text.toLowerCase())) {
                newSuggestions.add(new Suggestion(range, name));
            }
        }
        return Optional.of(newSuggestions);
    }

}
