package io.github.darkkronicle.advancedchat.interfaces;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.List;
import java.util.Optional;

public interface IMessageSuggestor {

    default Optional<List<Suggestions>> suggest(String text) {
        return Optional.empty();
    }

    default Optional<List<Suggestion>> suggestCurrentWord(String text, StringRange range) {
        return Optional.empty();
    }

}
