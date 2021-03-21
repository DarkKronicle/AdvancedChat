package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* A class used for helping filters find matches and act on them.
* Helps with Regular Expressions and means that we don't need this in each class.
 */
@Environment(EnvType.CLIENT)
public class SearchText {

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
    public static boolean isMatch(String input, String toMatch, Filter.FindType type) {
        if (type == Filter.FindType.UPPERLOWER) {
            Pattern pattern = Pattern.compile(Pattern.quote(toMatch), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            return matcher.find();
        } else if (type == Filter.FindType.LITERAL) {
            Pattern pattern = Pattern.compile(Pattern.quote(toMatch));
            Matcher matcher = pattern.matcher(input);
            return matcher.find();
        } else if (type == Filter.FindType.REGEX) {
            Pattern pattern = Pattern.compile(toMatch);
            Matcher matcher = pattern.matcher(input);
            return matcher.find();
        } else if (type == Filter.FindType.ALL) {
            return true;
        }
        return false;
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
    public static Optional<List<StringMatch>> findMatches(String input, String toMatch, Filter.FindType type) {
        Set<StringMatch> matches = new TreeSet<>();
        if (type == Filter.FindType.UPPERLOWER) {
            Pattern pattern = Pattern.compile(Pattern.quote(toMatch), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            int i = 0;
            while (matcher.find()) {
                matches.add(new StringMatch(matcher.group(), matcher.start(), matcher.end()));
                i++;
                if (i > 10) {
                    break;
                }
            }

        } else if (type == Filter.FindType.LITERAL) {
            Pattern pattern = Pattern.compile(Pattern.quote(toMatch));
            Matcher matcher = pattern.matcher(input);
            int i = 0;
            while (matcher.find()) {
                matches.add(new StringMatch(matcher.group(), matcher.start(), matcher.end()));
                i++;
                if (i > 10) {
                    break;
                }
            }
        } else if (type == Filter.FindType.REGEX) {
            Pattern pattern = Pattern.compile(toMatch);
            Matcher matcher = pattern.matcher(input);
            int i = 0;
            while (matcher.find()) {
                matches.add(new StringMatch(matcher.group(), matcher.start(), matcher.end()));
                i++;
                if (i > 10) {
                    break;
                }
            }
        } else if (type == Filter.FindType.ALL) {
            matches.add(new StringMatch(input, 0, input.length()));
        }
        if (matches.size() != 0) {
            return Optional.of(new ArrayList<>(matches));
        }
        return Optional.empty();
    }

    /**
     * Storage class that contains Matcher.match info. Used with {@link #findMatches(String, String, Filter.FindType)} and {@link #isMatch(String, String, Filter.FindType)}
     */
    @AllArgsConstructor
    public static class StringMatch implements Comparable<StringMatch> {
        public String match;
        public Integer start;
        public Integer end;

        @Override
        public int compareTo(StringMatch o) {
            return start.compareTo(o.start);
        }
    }

    public static PlayerListEntry getAuthor(ClientPlayNetworkHandler networkHandler, Text text) {
        if (networkHandler == null) {
            return null;
        }
        Optional<List<SearchText.StringMatch>> words = SearchText.findMatches(stripColorCodes(text.getString()), ConfigStorage.General.MESSAGE_OWNER_REGEX.config.getStringValue(), Filter.FindType.REGEX);
        if (!words.isPresent()) {
            return null;
        }
        // Start by just checking names and such
        PlayerListEntry player = null;
        StringMatch match = null;
        for (SearchText.StringMatch m : words.get()) {
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
            Optional<List<SearchText.StringMatch>> nameWords = SearchText.findMatches(name, ConfigStorage.General.MESSAGE_OWNER_REGEX.config.getStringValue(), Filter.FindType.REGEX);
            if (!nameWords.isPresent()) {
                continue;
            }
            entryMatches.put(e, nameWords.get());
        }
        for (SearchText.StringMatch m : words.get()) {
            for (Map.Entry<PlayerListEntry, List<StringMatch>> entry : entryMatches.entrySet()) {
                for (SearchText.StringMatch nm : entry.getValue()) {
                    if (nm.match.equals(m.match)) {
                        if (player != null && match.start <= m.start) {
                            return player;
                        }
                        return entry.getKey();
                    }
                }
            }
        }
        return null;
    }

    public static String stripColorCodes(String string) {
        return string.replaceAll("§.", "");
    }

}
