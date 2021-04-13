package io.github.darkkronicle.advancedchat.filters;

import io.github.darkkronicle.advancedchat.interfaces.IFilter;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.FluidText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Filter used for replacing matches in a Text
 */
@Environment(EnvType.CLIENT)
public class ReplaceFilter implements IFilter {

    public final String replaceTo;
    public final IMatchReplace type;
    public final ColorUtil.SimpleColor color;

    @Getter
    private ArrayList<IFilter> children = new ArrayList<>();

    public void addChild(IFilter filter) {
        children.add(filter);
    }

    public ReplaceFilter(String replaceTo, IMatchReplace type, ColorUtil.SimpleColor color) {
        this.replaceTo = replaceTo;
        this.type = type;
        this.color = color;
    }

    @Override
    public Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search) {
        // Grabs FluidText for easy mutability.
        if (type == null) {
            return Optional.empty();
        }
        if (type.matchesOnly()) {
            if (search.size() == 0) {
                return Optional.empty();
            }
            return type.filter(this, text, search);
        } else {
            return type.filter(this, text, null);
        }
    }
}
