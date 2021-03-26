package net.darkkronicle.advancedchat.interfaces;

import jdk.internal.jline.internal.Nullable;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.minecraft.text.Text;

import java.util.List;

public interface IMatchProcessor extends IMessageProcessor {

    @Override
    default boolean process(Text text, Text unfiltered) {
        return processMatches(text, (List<SearchUtils.StringMatch>) null);
    }

    boolean processMatches(Text text, @Nullable List<SearchUtils.StringMatch> matches);

    default boolean matchesOnly() {
        return true;
    }

}
