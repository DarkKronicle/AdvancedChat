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
    private CompletableFuture<AdvancedSuggestions> pendingSuggestions;
    @Getter
    private StringRange range;
    @Getter
    private List<AdvancedSuggestions> allSuggestions;

    private final TextFieldWidget textField;
    private final MinecraftClient client;

    public ChatSuggestor(TextFieldWidget textField) {
        this.textField = textField;
        this.client = MinecraftClient.getInstance();
    }

    public boolean isDone() {
        return pendingSuggestions != null && pendingSuggestions.isDone();
    }

    public List<AdvancedSuggestion> getSuggestions() {
        if (!isDone()) {
            return new ArrayList<>();
        }
        AdvancedSuggestions suggested = pendingSuggestions.join();
        range = suggested.getRange();
        List<AdvancedSuggestion> suggestions = modifySuggestions(pendingSuggestions.join());
        return suggestions;
    }

    public void updateCommandSuggestions() {
        updateCommandSuggestions(null);
    }

    public void updateCommandSuggestions(Runnable after) {
        allSuggestions = null;
        CommandDispatcher<CommandSource> commandDispatcher = client.player.networkHandler.getCommandDispatcher();
        this.pendingSuggestions = new CompletableFuture<>();
        CompletableFuture<Suggestions> suggests = commandDispatcher.getCompletionSuggestions(this.parse, getCursorIndex());
        suggests.thenRun(() -> pendingSuggestions.complete(AdvancedSuggestions.fromSuggestions(suggests.join())));
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
        ArrayList<AdvancedSuggestions> suggestions = new ArrayList<>();
        for (ChatSuggestorRegistry.ChatSuggestorOption option : ChatSuggestorRegistry.getInstance().getAll()) {
            if (!option.isActive()) {
                continue;
            }
            IMessageSuggestor suggestor = option.getOption();
            Optional<List<AdvancedSuggestions>> suggestion = suggestor.suggest(textField.getText());
            suggestion.ifPresent(suggestions::addAll);
        }
        this.allSuggestions = suggestions;
        this.pendingSuggestions = suggestMatching(wordIndex, startToCursor, suggestions);
    }

    private CompletableFuture<AdvancedSuggestions> suggestMatching(int start, String input, List<AdvancedSuggestions> other) {
//        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

        List<AdvancedSuggestion> newSuggestions = new ArrayList<>();
        String lastWord = input.substring(start);
        StringRange r = new StringRange(start, input.length());
        for (ChatSuggestorRegistry.ChatSuggestorOption option : ChatSuggestorRegistry.getInstance().getAll()) {
            if (!option.isActive()) {
                continue;
            }
            Optional<List<AdvancedSuggestion>> s = option.getOption().suggestCurrentWord(lastWord, r);
            s.ifPresent(newSuggestions::addAll);
        }

        for (AdvancedSuggestions suggestions : other) {
            if (suggestions.getRange().getStart() <= textField.getCursor() && suggestions.getRange().getEnd() >= textField.getCursor()) {
                newSuggestions.addAll(suggestions.getSuggestions());
            }
        }

        if (newSuggestions.size() <= 0) {
            return AdvancedSuggestions.empty();
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
        AdvancedSuggestions suggested = new AdvancedSuggestions(range, newSuggestions);

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

    private Map<AdvancedSuggestion, String> mapNames(AdvancedSuggestions suggestions) {
        HashMap<AdvancedSuggestion, String> map = new HashMap<>();
        for (AdvancedSuggestion s : suggestions.getSuggestions()) {
            map.put(s, removeIdentifier(s.getText()));
        }
        return map;
    }

    private List<AdvancedSuggestion> modifySuggestions(AdvancedSuggestions suggestions) {
        List<AdvancedSuggestion> newSuggestions;
        if (ConfigStorage.ChatSuggestor.REMOVE_IDENTIFIER.config.getBooleanValue()) {
            newSuggestions =  new ArrayList<>();
            Map<AdvancedSuggestion, String> names = mapNames(suggestions);
            for (Map.Entry<AdvancedSuggestion, String> entry : names.entrySet()) {
                if (Collections.frequency(names.values(), entry.getValue()) >= 2) {
                    newSuggestions.add(entry.getKey());
                } else {
                    newSuggestions.add(new AdvancedSuggestion(entry.getKey().getRange(), entry.getValue(), entry.getKey().getRender(), entry.getKey().getTooltip()));
                }
            }
        } else {
            newSuggestions = suggestions.getSuggestions();
        }
        return orderSuggestions(newSuggestions);
    }

    private List<AdvancedSuggestion> orderSuggestions(List<AdvancedSuggestion> suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        String lastWord = string.substring(getLastWord(string)).toLowerCase(Locale.ROOT);
        List<AdvancedSuggestion> minecraftSuggestions = Lists.newArrayList();
        List<AdvancedSuggestion> otherSuggestions = Lists.newArrayList();

        for (AdvancedSuggestion suggestion : suggestions) {
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
