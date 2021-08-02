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

/**
 * A maintainer of suggestions to suggest to the player.
 */
@Environment(EnvType.CLIENT)
public class ChatSuggestor {
    private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s+)");

    /**
     * Parsed command results
     */
    @Getter
    private ParseResults<CommandSource> parse;

    /**
     * Suggestions to complete
     */
    @Getter
    private CompletableFuture<AdvancedSuggestions> pendingSuggestions;

    /**
     * Range of suggestion
     */
    @Getter
    private StringRange range;

    /**
     * All completed suggestions
     */
    @Getter
    private List<AdvancedSuggestions> allSuggestions;

    private final TextFieldWidget textField;
    private final MinecraftClient client;

    public ChatSuggestor(TextFieldWidget textField) {
        this.textField = textField;
        this.client = MinecraftClient.getInstance();
    }

    /**
     * Checks to see if pending suggestions has completed
     * @return If suggestions are ready
     */
    public boolean isDone() {
        return pendingSuggestions != null && pendingSuggestions.isDone();
    }

    /**
     * Get all the suggestions
     * @return List of {@link AdvancedSuggestion}
     */
    public List<AdvancedSuggestion> getSuggestions() {
        if (!isDone()) {
            return new ArrayList<>();
        }
        AdvancedSuggestions suggested = pendingSuggestions.join();
        range = suggested.getRange();
        List<AdvancedSuggestion> suggestions = modifySuggestions(pendingSuggestions.join());
        return suggestions;
    }

    /**
     * Update command suggestions
     */
    public void updateCommandSuggestions() {
        updateCommandSuggestions(null);
    }

    /**
     * Update command suggestions and run something afterwards
     * @param after Runnable to run after suggestions have completed
     */
    public void updateCommandSuggestions(Runnable after) {
        allSuggestions = null;
        CommandDispatcher<CommandSource> commandDispatcher = client.player.networkHandler.getCommandDispatcher();
        pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, getCursorIndex()).thenApplyAsync(AdvancedSuggestions::fromSuggestions);
        if (after != null) {
            runAfterDone(after);
        }
    }

    /**
     * Update what's being parsed by a {@link StringReader}
     * @param stringReader StringReader which contains reading string
     */
    public void updateParse(StringReader stringReader) {
        CommandDispatcher<CommandSource> commandDispatcher = client.player.networkHandler.getCommandDispatcher();
        if (parse == null) {
            parse = commandDispatcher.parse(stringReader, client.player.networkHandler.getCommandSource());
        }
    }

    /**
     * Get's the index of the cursor in the chat field
     * @return Cursor index
     */
    private int getCursorIndex() {
        return textField.getCursor();
    }

    /**
     * Update's suggestions specifically for chat (not command).
     */
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

    /**
     * Build suggestions and suggest
     *
     * @param start Start of input
     * @param input Input
     * @param other Other suggestions
     * @return CompletableFuture of the suggestions
     */
    private CompletableFuture<AdvancedSuggestions> suggestMatching(int start, String input, List<AdvancedSuggestions> other) {
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

    /**
     * Removes the identifier section of a suggestion. ("identifier:name")
     * @param string Input to remove identifier
     * @return String without the identifier. If there is no identifier it will just return the original one.
     */
    private String removeIdentifier(String string) {
        String[] text = string.split(":");
        if (text.length < 2) {
            return string;
        }
        return String.join("", Arrays.copyOfRange(text, 1, text.length));
    }

    /**
     * Create's a map of {@link AdvancedSuggestion} and suggestion name. Used to remove identifiers.
     * @param suggestions List of suggestions to map
     * @return Map which contains the suggestion and the name.
     */
    private Map<AdvancedSuggestion, String> mapNames(AdvancedSuggestions suggestions) {
        HashMap<AdvancedSuggestion, String> map = new HashMap<>();
        for (AdvancedSuggestion s : suggestions.getSuggestions()) {
            map.put(s, removeIdentifier(s.getText()));
        }
        return map;
    }

    /**
     * Modifies suggestions to comply with the formatting that is wanted.
     * @param suggestions Suggestions to modify.
     * @return List of filtered {@link AdvancedSuggestion}
     */
    private List<AdvancedSuggestion> modifySuggestions(AdvancedSuggestions suggestions) {
        List<AdvancedSuggestion> newSuggestions;
        if (ConfigStorage.ChatSuggestor.REMOVE_IDENTIFIER.config.getBooleanValue()) {
            // Remove identifier
            newSuggestions =  new ArrayList<>();
            Map<AdvancedSuggestion, String> names = mapNames(suggestions);
            // Map
            for (Map.Entry<AdvancedSuggestion, String> entry : names.entrySet()) {
                // Check if it appears more than once (then we keep it)
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

    /**
     * Order's suggestions based off of deliminator.
     * @param suggestions List of {@link AdvancedSuggestion} to order
     * @return Ordered suggestions
     */
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
        Collections.sort(minecraftSuggestions);
        Collections.sort(otherSuggestions);
        minecraftSuggestions.addAll(otherSuggestions);
        return minecraftSuggestions;
    }

    /**
     * Get's the index of the last word from an input string. (from the end to the last space)
     * @param input Input string
     * @return Last word of string
     */
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

    /**
     * Remove the parse
     */
    public void invalidateParse() {
        this.parse = null;
    }

}
