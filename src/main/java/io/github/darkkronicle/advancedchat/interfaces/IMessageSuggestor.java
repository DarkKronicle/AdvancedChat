package io.github.darkkronicle.advancedchat.interfaces;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.darkkronicle.advancedchat.chat.AdvancedSuggestion;
import io.github.darkkronicle.advancedchat.chat.AdvancedSuggestions;

import java.util.List;
import java.util.Optional;

public interface IMessageSuggestor {

    default Optional<List<AdvancedSuggestions>> suggest(String text) {
        return Optional.empty();
    }

    default Optional<List<AdvancedSuggestion>> suggestCurrentWord(String text, StringRange range) {
        return Optional.empty();
    }

}
