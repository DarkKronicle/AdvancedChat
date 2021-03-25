package net.darkkronicle.advancedchat.interfaces;

import jdk.internal.jline.internal.Nullable;
import net.darkkronicle.advancedchat.filters.ReplaceFilter;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public interface IMatchReplace extends IMessageFilter {

    default boolean matchesOnly() {
        return true;
    }

    Optional<Text> filter(ReplaceFilter filter, SplitText text, @Nullable List<SearchUtils.StringMatch> matches);

    default Optional<Text> filter(Text text) {
        return Optional.empty();
    }

}
