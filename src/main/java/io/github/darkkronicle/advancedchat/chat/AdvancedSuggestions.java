package io.github.darkkronicle.advancedchat.chat;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A holder of {@link AdvancedSuggestion}
 *
 * Maintains the start/stop range and suggestions in that range
 */
@Environment(EnvType.CLIENT)
public class AdvancedSuggestions {

    @Getter
    private final List<AdvancedSuggestion> suggestions;
    @Getter
    private StringRange range;

    /**
     * Empty suggestions
     */
    public final static AdvancedSuggestions EMPTY = new AdvancedSuggestions(StringRange.at(0), new ArrayList<>());

    /**
     * Future of EMPTY
     */
    public static CompletableFuture<AdvancedSuggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    public AdvancedSuggestions(StringRange range, List<AdvancedSuggestion> suggestions) {
        this.suggestions = suggestions;
        suggestions.sort(AdvancedSuggestion::compareToIgnoreCase);
        if (range != null) {
            this.range = range;
        } else {
            setRange();
        }
    }

    private void setRange() {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (AdvancedSuggestion s : suggestions) {
            start = Math.min(s.getRange().getStart(), start);
            end = Math.max(s.getRange().getEnd(), end);
        }
        this.range = new StringRange(start, end);
    }

    /**
     * Converts {@link Suggestions} into {@link AdvancedSuggestions}
     * @param suggestions Suggestions to convert
     * @return Converted object
     */
    public static AdvancedSuggestions fromSuggestions(Suggestions suggestions) {
        List<AdvancedSuggestion> s = new ArrayList<>();
        for (Suggestion suggestion : suggestions.getList()) {
            if (suggestion instanceof AdvancedSuggestion) {
                s.add((AdvancedSuggestion) suggestion);
            } else {
                s.add(AdvancedSuggestion.fromSuggestion(suggestion));
            }
        }
        return new AdvancedSuggestions(suggestions.getRange(), s);
    }

}
