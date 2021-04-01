package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import net.darkkronicle.advancedchat.chat.MessageOwner;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.config.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* A class used for helping filters find matches and act on them.
* Helps with Regular Expressions and means that we don't need this in each class.
 */
@Environment(EnvType.CLIENT)
@UtilityClass
public class SearchUtils {

    /**
     * Method to see if there is a match somewhere with a string with an expression.
     * Is similar to {@link #findMatches(String, String, Filter.FindType)} just less expensive since
     * it doesn't need to find every match.
     *
     * @param input String to search.
     * @param toMatch Expression to find.
     * @param type How toMatch should be interpreted.
     * @return If a match is found.
     */
    public boolean isMatch(String input, String toMatch, Filter.FindType type) {
        if (type == Filter.FindType.ALL) {
            return true;
        }
        Pattern pattern = compilePattern(toMatch, type);
        if (pattern == null) {
            return false;
        }
        return pattern.matcher(input).find();
    }

    /**
     * Compiles a {@link Pattern} for the specified {@link Filter.FindType}
     *
     * @param toMatch Match string
     * @param type Find type
     * @return Compiled pattern
     */
    public Pattern compilePattern(String toMatch, Filter.FindType type) {
        switch (type) {
            case UPPERLOWER:
                return Pattern.compile(Pattern.quote(toMatch), Pattern.CASE_INSENSITIVE);
            case LITERAL:
                return Pattern.compile(Pattern.quote(toMatch));
            case REGEX:
                return Pattern.compile(toMatch);
            case ALL:
                return Pattern.compile(".+");
        }
        return null;
    }

    /**
     * Method to find all matches within a string.
     * Is similar to {@link #isMatch(String, String, Filter.FindType)}}. This method just finds every
     * match and returns it.
     *
     * @param input String to search.
     * @param toMatch Expression to find.
     * @param type How toMatch should be interpreted.
     * @return An Optional containing a list of {@link StringMatch}
     */
    public Optional<List<StringMatch>> findMatches(String input, String toMatch, Filter.FindType type) {
        if (type == Filter.FindType.ALL) {
            return Optional.of(Collections.singletonList(new StringMatch(input, 0, input.length())));
        }
        Pattern pattern = compilePattern(toMatch, type);
        if (pattern == null) {
            return Optional.empty();
        }
        Set<StringMatch> matches = new TreeSet<>();
        Matcher matcher = pattern.matcher(input);
        addMatches(matches, matcher);
        if (matches.size() != 0) {
            return Optional.of(new ArrayList<>(matches));
        }
        return Optional.empty();
    }

    private void addMatches(Set<StringMatch> matches, Matcher matcher) {
        int i = 0;
        while (matcher.find() && i < 1000) {
            matches.add(new StringMatch(matcher.group(), matcher.start(), matcher.end()));
            i++;
        }
    }

    /**
     * Storage class that contains Matcher.match info. Used with {@link #findMatches(String, String, Filter.FindType)} and {@link #isMatch(String, String, Filter.FindType)}
     */
    @AllArgsConstructor
    public static class StringMatch implements Comparable<StringMatch> {
        /**
         * The content that was matched
         */
        public String match;

        /**
         * The index of the start of the match
         */
        public Integer start;

        /**
         * The index of the end of the match
         */
        public Integer end;

        @Override
        public int compareTo(StringMatch o) {
            return start.compareTo(o.start);
        }
    }

    /**
     * Get the author of a message using regex
     *
     * @param networkHandler Network handler to get player data
     * @param text Text to search
     * @return Owner of the message
     */
    public MessageOwner getAuthor(ClientPlayNetworkHandler networkHandler, Text text) {
        if (networkHandler == null) {
            return null;
        }
        Optional<List<SearchUtils.StringMatch>> words = SearchUtils.findMatches(stripColorCodes(text.getString()), ConfigStorage.General.MESSAGE_OWNER_REGEX.config.getStringValue(), Filter.FindType.REGEX);
        if (!words.isPresent()) {
            return null;
        }
        // Start by just checking names and such
        PlayerListEntry player = null;
        StringMatch match = null;
        for (SearchUtils.StringMatch m : words.get()) {
            if (player != null) {
                break;
            }
            for (PlayerListEntry e : networkHandler.getPlayerList()) {
                // Easy mode
                if ((e.getDisplayName() != null && m.match.equals(stripColorCodes(e.getDisplayName().getString()))) || m.match.equals(e.getProfile().getName())) {
                    player = e;
                    match = m;
                    break;
                }
            }
        }
        // Check for ***everything***
        HashMap<PlayerListEntry, List<StringMatch>> entryMatches = new HashMap<>();
        for (PlayerListEntry e : networkHandler.getPlayerList()) {
            String name = stripColorCodes(e.getDisplayName() == null ? e.getProfile().getName() : e.getDisplayName().getString());
            Optional<List<SearchUtils.StringMatch>> nameWords = SearchUtils.findMatches(name, ConfigStorage.General.MESSAGE_OWNER_REGEX.config.getStringValue(), Filter.FindType.REGEX);
            if (!nameWords.isPresent()) {
                continue;
            }
            entryMatches.put(e, nameWords.get());
        }
        for (SearchUtils.StringMatch m : words.get()) {
            for (Map.Entry<PlayerListEntry, List<StringMatch>> entry : entryMatches.entrySet()) {
                for (SearchUtils.StringMatch nm : entry.getValue()) {
                    if (nm.match.equals(m.match)) {
                        if (player != null && match.start <= m.start) {
                            return new MessageOwner(match.match, player);
                        }
                        return new MessageOwner(nm.match, entry.getKey());
                    }
                }
            }
        }
        return null;
    }

    public String stripColorCodes(String string) {
        return string.replaceAll("§.", "");
    }

    private final TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    /**
     * Turns a number into a Roman Numeral.
     *
     * Example: 4 -> IV
     *
     * https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java/12968022
     *
     * @param number Example to convert to
     * @return String or Roman Numeral
     */
    public String toRoman(int number) {
        boolean neg = false;
        if (number == 0) {
            return "O";
        }
        if (number < 0) {
            neg = true;
            number = -1 * number;
        }
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        if (neg) {
            return "-" + map.get(l) + toRoman(number - l);
        } else {
            return map.get(l) + toRoman(number - l);
        }
    }

}
