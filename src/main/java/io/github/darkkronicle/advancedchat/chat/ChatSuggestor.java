package io.github.darkkronicle.advancedchat.chat;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.darkkronicle.advancedchat.chat.registry.ChatSuggestorRegistry;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ChatSuggestor {
    private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s+)");
    @Getter
    private ParseResults<CommandSource> parse;
    @Getter
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Getter
    private StringRange range;
    @Getter
    private List<Suggestions> allSuggestions;

    private final TextFieldWidget textField;
    private final MinecraftClient client;

    public ChatSuggestor(TextFieldWidget textField) {
        this.textField = textField;
        this.client = MinecraftClient.getInstance();
    }

    public boolean isDone() {
        return pendingSuggestions != null && pendingSuggestions.isDone();
    }

    public List<Suggestion> getSuggestions() {
        if (!isDone()) {
            return new ArrayList<>();
        }
        Suggestions suggested = pendingSuggestions.join();
        range = suggested.getRange();
        List<Suggestion> suggestions = modifySuggestions(pendingSuggestions.join());
        return suggestions;
    }

    public void updateCommandSuggestions() {
        updateCommandSuggestions(null);
    }

    public void updateCommandSuggestions(Runnable after) {
        allSuggestions = null;
        CommandDispatcher<CommandSource> commandDispatcher = client.player.networkHandler.getCommandDispatcher();
        this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, getCursorIndex());
        if (after != null) {
            runAfterDone(after);
        }
    }

    public void updateParse(StringReader stringReader) {
        CommandDispatcher<CommandSource> commandDispatcher = client.player.networkHandler.getCommandDispatcher();
        if (parse == null) {
            parse = commandDispatcher.parse(stringReader, client.player.networkHandler.getCommandSource());
        }
    }

    private int getCursorIndex() {
        return textField.getCursor();
    }

    public void updateChatSuggestions() {
        String startToCursor = textField.getText().substring(0, getCursorIndex());
        int wordIndex = getLastWord(startToCursor);
        ArrayList<Suggestions> suggestions = new ArrayList<>();
        for (ChatSuggestorRegistry.ChatSuggestorOption option : ChatSuggestorRegistry.getInstance().getAll()) {
            if (!option.isActive()) {
                continue;
            }
            IMessageSuggestor suggestor = option.getOption();
            Optional<List<Suggestions>> suggestion = suggestor.suggest(textField.getText());
            suggestion.ifPresent(suggestions::addAll);
        }
        this.allSuggestions = suggestions;
        this.pendingSuggestions = suggestMatching(wordIndex, startToCursor, suggestions);
    }

    private CompletableFuture<Suggestions> suggestMatching(int start, String input, List<Suggestions> other) {
//        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

        List<Suggestion> newSuggestions = new ArrayList<>();
        String lastWord = input.substring(start);
        StringRange r = new StringRange(start, input.length());
        for (ChatSuggestorRegistry.ChatSuggestorOption option : ChatSuggestorRegistry.getInstance().getAll()) {
            if (!option.isActive()) {
                continue;
            }
            Optional<List<Suggestion>> s = option.getOption().suggestCurrentWord(lastWord, r);
            s.ifPresent(newSuggestions::addAll);
        }

        for (Suggestions suggestions : other) {
            if (suggestions.getRange().getStart() <= textField.getCursor() && suggestions.getRange().getEnd() >= textField.getCursor()) {
                newSuggestions.addAll(suggestions.getList());
            }
        }

        if (newSuggestions.size() <= 0) {
            return Suggestions.empty();
        }

        int min = -1;
        int max = 0;
        for (Suggestion suggestion : newSuggestions) {
            if (min == -1) {
                min = suggestion.getRange().getStart();
            }
            min = Math.min(suggestion.getRange().getStart(), min);
            max = Math.max(suggestion.getRange().getEnd(), max);
        }

        range = new StringRange(min, max);
        Suggestions suggested = new Suggestions(range, newSuggestions);

        return CompletableFuture.completedFuture(suggested);
    }

    public void runAfterDone(Runnable runnable) {
        pendingSuggestions.thenRun(runnable);
    }

    private String removeIdentifier(String string) {
        String[] text = string.split(":");
        if (text.length < 2) {
            return string;
        }
        return String.join("", Arrays.copyOfRange(text, 1, text.length));
    }

    private Map<Suggestion, String> mapNames(Suggestions suggestions) {
        HashMap<Suggestion, String> map = new HashMap<>();
        for (Suggestion s : suggestions.getList()) {
            map.put(s, removeIdentifier(s.getText()));
        }
        return map;
    }

    private List<Suggestion> modifySuggestions(Suggestions suggestions) {
        List<Suggestion> newSuggestions;
        if (ConfigStorage.ChatSuggestor.REMOVE_IDENTIFIER.config.getBooleanValue()) {
            newSuggestions =  new ArrayList<>();
            Map<Suggestion, String> names = mapNames(suggestions);
            for (Map.Entry<Suggestion, String> entry : names.entrySet()) {
                if (Collections.frequency(names.values(), entry.getValue()) >= 2) {
                    newSuggestions.add(entry.getKey());
                } else {
                    newSuggestions.add(new Suggestion(entry.getKey().getRange(), entry.getValue(), entry.getKey().getTooltip()));
                }
            }
        } else {
            newSuggestions = suggestions.getList();
        }
        return orderSuggestions(newSuggestions);
    }

    private List<Suggestion> orderSuggestions(List<Suggestion> suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        String lastWord = string.substring(getLastWord(string)).toLowerCase(Locale.ROOT);
        List<Suggestion> minecraftSuggestions = Lists.newArrayList();
        List<Suggestion> otherSuggestions = Lists.newArrayList();

        for (Suggestion suggestion : suggestions) {
            if (!suggestion.getText().startsWith(lastWord) && !suggestion.getText().startsWith("minecraft:" + lastWord)) {
                otherSuggestions.add(suggestion);
            } else {
                minecraftSuggestions.add(suggestion);
            }
        }

        minecraftSuggestions.addAll(otherSuggestions);
        return minecraftSuggestions;
    }

    public static int getLastWord(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return 0;
        }
        int index = 0;
        Matcher matcher = SPACE_PATTERN.matcher(input);
        while (matcher.find()) {
            index = matcher.end();
        }

        return index;
    }

    public void invalidateParse() {
        this.parse = null;
    }

}
