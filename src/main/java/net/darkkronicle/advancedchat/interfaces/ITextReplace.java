package net.darkkronicle.advancedchat.interfaces;

import jdk.internal.jline.internal.Nullable;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public interface ITextReplace {

    default boolean matchesOnly() {
        return true;
    }

    Optional<Text> filter(ReplaceFilter filter, SplitText text, @Nullable List<SearchText.StringMatch> matches);

}
