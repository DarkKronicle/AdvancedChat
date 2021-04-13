package io.github.darkkronicle.advancedchat.filters;

import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.interfaces.IFilter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ForwardFilter implements IFilter {

    private final MatchProcessorRegistry registry;

    public ForwardFilter(MatchProcessorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered,SearchResult search) {
        boolean forward = true;
        for (MatchProcessorRegistry.MatchProcessorOption p : registry.getAll()) {
            if (!p.isActive()) {
                continue;
            }
            if (!p.getOption().matchesOnly() && !search.getMatches().isEmpty()) {
                if (p.getOption().processMatches(text, unfiltered, null)) {
                    forward = false;
                }
            } else if (!search.getMatches().isEmpty()) {
                if (p.getOption().processMatches(text, unfiltered, search.getMatches())) {
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
