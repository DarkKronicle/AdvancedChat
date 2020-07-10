package net.darkkronicle.advancedchat.util;

import net.darkkronicle.advancedchat.storage.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchText {

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


    public static Optional<List<StringMatch>> findMatch(String input, String toMatch, Filter.FindType type) {
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

    public static class StringMatch {
        public String match;
        public int start;
        public int end;

        public StringMatch(String match, int start, int end) {
            this.match = match;
            this.start = start;
            this.end = end;
        }
    }
}
