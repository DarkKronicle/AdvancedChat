/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package net.darkkronicle.advancedchat.util;

import lombok.AllArgsConstructor;
import net.darkkronicle.advancedchat.storage.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* A class used for helping filters find matches and act on them.
* Helps with Regular Expressions and means that we don't need this in each class.
 */
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

}
