package io.github.darkkronicle.advancedchat.chat.suggestors;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.softcorporation.suggester.BasicSuggester;
import com.softcorporation.suggester.Suggestion;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.tools.SpellCheck;
import com.softcorporation.suggester.util.SuggesterException;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SpellCheckSuggestor implements IMessageSuggestor {

    private final BasicDictionary dictionary;
    private final BasicSuggester suggester;

    public SpellCheckSuggestor() throws SuggesterException {
        dictionary = new BasicDictionary("file://" + new File("./config/advancedchat/english.zip").getAbsolutePath().replace("./", ""));
        suggester = new BasicSuggester();
        suggester.attach(dictionary);
    }

    public static Supplier<IMessageSuggestor> newWithCatch() {
        return () -> {
            try {
                return new SpellCheckSuggestor();
            } catch (Exception e) {
                LogManager.getLogger().log(Level.ERROR, "[AdvancedChat] {}", "Couldn't load SpellCheckSuggestor", e);
                return null;
            }
        };
    }

    @Override
    public Optional<List<Suggestions>> suggest(String text) {
        SpellCheck spellCheck = new SpellCheck();
        spellCheck.setSuggestionLimit(5);
        try {
            spellCheck.setSuggester(suggester);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        ArrayList<Suggestions> suggestions = new ArrayList<>();
        try {
            spellCheck.setText(text);
            spellCheck.check();
            while (spellCheck.hasMisspelt()) {
                List<Suggestion> s = spellCheck.getSuggestions();
                if (s.isEmpty()) {
                    // If it can't help you, don't flag it
                    spellCheck.checkNext();
                    continue;
                }
                int pos = spellCheck.getMisspeltOffset();
                int length = spellCheck.getMisspeltLength();
                String word = spellCheck.getMisspelt();
                StringRange range = new StringRange(pos, pos + length);
                suggestions.add(new Suggestions(range, convertSuggestions(s, range, word)));
                spellCheck.checkNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(suggestions);
    }

    private static List<com.mojang.brigadier.suggestion.Suggestion> convertSuggestions(List<Suggestion> suggestions, StringRange range, String string) {
        List<com.mojang.brigadier.suggestion.Suggestion> replacements = new ArrayList<>();
        for (Suggestion s : suggestions) {
            replacements.add(new com.mojang.brigadier.suggestion.Suggestion(range, s.getWord()));
        }
        return replacements;
    }
}
