package io.github.darkkronicle.advancedchat.filters;

import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
    public Optional<FluidText> filter(FluidText text) {
        return filter(text, text, new ArrayList<>());
    }

    public Optional<FluidText> filter(FluidText text, FluidText unfiltered, ArrayList<IMessageProcessor> processed) {
        Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(text.getString(), super.filterString, findType);
        boolean forward = true;
        for (IMatchProcessor p : processors) {
            if (processed.contains(p)) {
                continue;
            }
            if (!p.matchesOnly() && !omatches.isPresent()) {
                if (p.processMatches(text, unfiltered, null)) {
                    processed.add(p);
                    forward = false;
                }
            } else if (omatches.isPresent()) {
                if (p.processMatches(text, unfiltered, omatches.get())) {
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
