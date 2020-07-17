package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

import java.util.ArrayList;
import java.util.Optional;

// Filter that handles all other filters.
@Environment(EnvType.CLIENT)
public class MainFilter extends AbstractFilter {

    @Getter
    private ArrayList<ColorFilter> colorFilters = new ArrayList<>();

    private ArrayList<AbstractFilter> filters = new ArrayList<>();

    public MainFilter() {
        loadFilters();
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        // Filters through all filters.
        StringRenderable modifiedtext = null;
        for (AbstractFilter filter : filters) {
            Optional<StringRenderable> newtext = filter.filter(text);
            if (newtext.isPresent()) {
                modifiedtext = newtext.get();
            }
        }
        if (modifiedtext != null) {
            return Optional.of(modifiedtext);

        }
        return Optional.empty();
    }

    /**
     * <h1>LoadFilters</h1>
     * Loads filters that are stored in ConfigStorage.
     * Converts {@link Filter} into an {@link AbstractFilter}
     */
    public void loadFilters() {
       filters = new ArrayList<>();
       colorFilters = new ArrayList<>();
       for (Filter filter : AdvancedChat.configStorage.filters) {
           // If it replaces anything.
           if (filter.getReplaceType() != Filter.ReplaceType.NONE) {
               if (filter.isReplaceTextColor()) {
                   filters.add(new ReplaceFilter(filter.getFindString(), filter.getReplaceTo(), filter.getFindType(), filter.getReplaceType(), filter.getColor()));
               } else {
                   filters.add(new ReplaceFilter(filter.getFindString(), filter.getReplaceTo(), filter.getFindType(), filter.getReplaceType(), null));
               }
           }
           if (filter.isReplaceBackgroundColor()) {
                colorFilters.add(new ColorFilter(filter.getFindString(), filter.getFindType(), filter.getColor()));
           }
       }
    }
}
