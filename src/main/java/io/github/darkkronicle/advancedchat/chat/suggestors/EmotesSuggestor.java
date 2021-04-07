package io.github.darkkronicle.advancedchat.chat.suggestors;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EmotesSuggestor implements IMessageSuggestor {

    @Override
    public Optional<List<Suggestions>> suggest(String string) {
        if (!string.contains(":")) {
            return Optional.empty();
        }
        ArrayList<Suggestions> suggest = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ':') {
                continue;
            }
            int start = i;
            int end;
            while (true) {
                i++;
                if (i >= string.length()) {
                    end = i;
                    break;
                }
                char n = string.charAt(i);
                if (n == ' ' || n == ':') {
                    i--;
                    end = i;
                    break;
                }
            }
            if (end - start < 1) {
                break;
            }
            StringRange range = new StringRange(start, end);
            suggest.add(new Suggestions(range, getSuggestions(string.substring(start, end), range)));
        }
        return Optional.of(suggest);
    }

    private List<Suggestion> getSuggestions(String current, StringRange range) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        for (String s : new String[]{"o((*^▽^*))o", "ÒwÓ"}) {
            suggestions.add(new Suggestion(range, s));
        }
        return suggestions;
    }

}
