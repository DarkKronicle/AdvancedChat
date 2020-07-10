package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import net.darkkronicle.advancedchat.storage.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* A class used for helping filters find matches and act on them.
* Helps with Regular Expressions and means that we don't need this in each class.
 */
public class SearchText {

    /** <h1>isMatch</h1>
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
        }
        return false;
    }

    /** <h1>findMatches</h1>
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
        ArrayList<StringMatch> matches = new ArrayList<>();
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
        }
        if (matches.size() != 0) {
            return Optional.of(matches);
        }
        return Optional.empty();
    }

    /**
     * <h1>StringMatch</h1>
     * Storage class that contains Matcher.match info. Used with {@link #findMatches(String, String, Filter.FindType)} and {@link #isMatch(String, String, Filter.FindType)}
     */
    @AllArgsConstructor
    public static class StringMatch {
        public String match;
        public int start;
        public int end;
    }

}
