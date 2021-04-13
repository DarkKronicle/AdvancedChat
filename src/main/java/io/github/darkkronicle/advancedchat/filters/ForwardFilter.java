package io.github.darkkronicle.advancedchat.filters;

import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ForwardFilter extends AbstractFilter {

    private final MatchProcessorRegistry registry;

    public ForwardFilter(String findString, Filter.FindType findType, MatchProcessorRegistry registry) {
        super(findString, findType);
        this.registry = registry;
    }

    @Override
    public Optional<FluidText> filter(FluidText text) {
        return filter(text, text, new ArrayList<>());
    }

    public Optional<FluidText> filter(FluidText text, FluidText unfiltered, ArrayList<IMessageProcessor> processed) {
        Optional<List<StringMatch>> omatches = SearchUtils.findMatches(text.getString(), super.filterString, findType);
        boolean forward = true;
        for (MatchProcessorRegistry.MatchProcessorOption p : registry.getAll()) {
            if (!p.isActive() || processed.contains(p.getOption())) {
                continue;
            }
            if (!p.getOption().matchesOnly() && !omatches.isPresent()) {
                if (p.getOption().processMatches(text, unfiltered, null)) {
                    processed.add(p.getOption());
                    forward = false;
                }
            } else if (omatches.isPresent()) {
                if (p.getOption().processMatches(text, unfiltered, omatches.get())) {
                    processed.add(p.getOption());
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
