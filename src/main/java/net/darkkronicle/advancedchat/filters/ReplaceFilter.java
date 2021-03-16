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

package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SimpleText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Filter used for replacing matches in a Text
 */
@Environment(EnvType.CLIENT)
public class ReplaceFilter extends AbstractFilter {

    private String replaceTo;
    private Filter.ReplaceType type;
    private ColorUtil.SimpleColor color;

    @Getter
    private ArrayList<AbstractFilter> children = new ArrayList<>();

    public void addChild(AbstractFilter filter) {
        children.add(filter);
    }

    public ReplaceFilter(String filterString, String replaceTo, Filter.FindType findType, Filter.ReplaceType type, ColorUtil.SimpleColor color) {
        super.filterString = filterString;
        this.replaceTo = replaceTo;
        this.type = type;
        this.color = color;
        super.findType = findType;
    }

    @Override
    public Optional<Text> filter(Text text) {
        // Grabs SplitText for easy mutability.
        SplitText splitText = new SplitText(text);
        if (type == Filter.ReplaceType.CHILDREN) {
            Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatches(splitText.getFullMessage(), super.filterString, findType);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }

            // We don't want new filters to modify what old filters would already have done.
            // It would lead to repeats of words, and just other kinds of messes.
            // To combat this we modify all the matches that haven't been matched yet based off of the new string length.
            ArrayList<SearchText.StringMatch> matches = new ArrayList<>(omatches.get());
            for (int i = 0; i < matches.size(); i++) {
                SearchText.StringMatch match = matches.get(i);
                SplitText current = splitText.truncate(match);
                if (current == null) {
                    continue;
                }
                for (AbstractFilter filter : children) {
                    Optional<Text> filteredtext = filter.filter(current.getText());
                    if (filteredtext.isPresent()) {
                        HashMap<SearchText.StringMatch, SplitText.StringInsert> toReplace = new HashMap<>();

                        // Get old length and new length. As well as modify the message that is currently being modified
                        // in the match
                        int oldLength = current.getFullMessage().length();
                        current = new SplitText(filteredtext.get());
                        int newLength = current.getFullMessage().length();
                        int modifyLength = newLength - oldLength;
                        // Take the new length and figure out how much each match needs to move to have it work.
                        for (int j = i + 1; j < matches.size(); j++) {
                            SearchText.StringMatch m = matches.get(j);
                            m.start += modifyLength;
                            m.end += modifyLength;
                        }
                        // Put in the new simple text for easy use
                        final SplitText toAdd = current;
                        toReplace.put(match, (current1, match1) -> toAdd);
                        // Replace the match
                        splitText.replaceStrings(toReplace);
                    }
                }
            }
            return Optional.of(splitText.getText());
        } else if (type == Filter.ReplaceType.ONLYMATCH) {
            Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatches(splitText.getFullMessage(), super.filterString, findType);
            if (!omatches.isPresent()) {
                return Optional.empty();
            }
            HashMap<SearchText.StringMatch, SplitText.StringInsert> matches = new HashMap<>();
            for (SearchText.StringMatch m : omatches.get()) {
                matches.put(m, (current, match) -> new SplitText(SimpleText.withColor(replaceTo.replaceAll("%MATCH%", match.match), color)));
            }
            splitText.replaceStrings(matches);
            return Optional.of(splitText.getText());

        } else if (type == Filter.ReplaceType.FULLMESSAGE) {
            Optional<List<SearchText.StringMatch>> matches = SearchText.findMatches(splitText.getFullMessage(), super.filterString, findType);
            if (matches.isPresent()) {
                StringBuilder match = new StringBuilder();
                for (SearchText.StringMatch m : matches.get()) {
                    match.append(m.match);
                }
                if (color == null) {
                    for (Text t : text.getSiblings()) {
                        if (t.getStyle().getColor() != null) {
                            color = new ColorUtil.SimpleColor(t.getStyle().getColor().getRgb());
                        }
                    }
                }
                SimpleText toReplace = new SimpleText(replaceTo.replaceAll("%MATCH%", match.toString()), Style.EMPTY);
                if (color != null) {
                    Style original = Style.EMPTY;
                    TextColor textColor = TextColor.fromRgb(color.color());
                    original = original.withColor(textColor);
                    toReplace = toReplace.withStyle(original);
                }
                return Optional.of(new SplitText(toReplace).getText());

            }
        }
        return Optional.empty();
    }
}
