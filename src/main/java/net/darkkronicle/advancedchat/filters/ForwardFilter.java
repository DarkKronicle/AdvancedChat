package net.darkkronicle.advancedchat.filters;

import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import net.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ForwardFilter extends AbstractFilter {

    private final ArrayList<IMatchProcessor> processors;

    public ForwardFilter(String findString, Filter.FindType findType, List<IMatchProcessor> processors) {
        super(findString, findType);
        this.processors = new ArrayList<>(processors);
    }

    @Override
    public Optional<Text> filter(Text text) {
        return filter(text, new ArrayList<>());
    }

    public Optional<Text> filter(Text text, ArrayList<IMessageProcessor> processed) {
        Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(text.getString(), super.filterString, findType);
        boolean forward = true;
        for (IMatchProcessor p : processors) {
            if (processed.contains(p)) {
                continue;
            }
            if (!p.matchesOnly() && !omatches.isPresent()) {
                if (p.processMatches(text, null)) {
                    processed.add(p);
                    forward = false;
                }
            } else if (omatches.isPresent()) {
                if (p.processMatches(text, omatches.get())) {
                    processed.add(p);
                    forward = false;
                }
            }
        }
        if (!forward) {
            return Optional.of(ChatDispatcher.TERMINATE);
        }
        return Optional.empty();
    }

}
