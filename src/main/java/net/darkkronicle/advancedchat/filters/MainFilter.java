package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.config.Filter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
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
    public Optional<Text> filter(Text text) {
        // Filters through all filters.

        Text modifiedtext = null;
        for (AbstractFilter filter : filters) {
            Optional<Text> newtext = filter.filter(text);
            if (newtext.isPresent()) {
                modifiedtext = newtext.get();
                text = modifiedtext;
            }
        }
        if (modifiedtext != null) {
            return Optional.of(modifiedtext);

        }
        return Optional.empty();
    }

    /**
     * Loads filters that are stored in ConfigStorage.
     * Converts {@link Filter} into an {@link AbstractFilter}
     */
    public void loadFilters() {
       filters = new ArrayList<>();
       colorFilters = new ArrayList<>();
       for (Filter filter : ConfigStorage.FILTERS) {
           // If it replaces anything.
            List<AbstractFilter> afilter = createFilter(filter);
            if (afilter != null) {
                for (AbstractFilter f : afilter) {
                    if (f instanceof ColorFilter) {
                        colorFilters.add((ColorFilter) f);
                    } else {
                        filters.add(f);
                    }
                }
            }

       }
    }

    public static List<AbstractFilter> createFilter(Filter filter) {
        if (!filter.getActive().config.getBooleanValue()) {
            return null;
        }
        ArrayList<AbstractFilter> filters = new ArrayList<>();
        if (filter.getReplace() != Filter.ReplaceType.NONE) {
            if (filter.getReplace() == Filter.ReplaceType.CHILDREN) {
                ReplaceFilter f = new ReplaceFilter(filter.getFindString().config.getStringValue(), filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getFind(), filter.getReplace(), null);
                if (filter.getChildren() != null) {
                    for (Filter child : filter.getChildren()) {
                        List<AbstractFilter> childf = createFilter(child);
                        if (childf != null) {
                            for (AbstractFilter childfilter : childf) {
                                f.addChild(childfilter);
                            }
                        }
                    }
                }
                filters.add(f);
            } else if (filter.getReplaceTextColor().config.getBooleanValue()) {
                filters.add(new ReplaceFilter(filter.getFindString().config.getStringValue(), filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getFind(), filter.getReplace(), filter.getTextColor().config.getSimpleColor()));
            } else {
                filters.add(new ReplaceFilter(filter.getFindString().config.getStringValue(), filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getFind(), filter.getReplace(), null));
            }
        }
        if (filter.getSound() != Filter.NotifySound.NONE) {
            filters.add(new NotifyFilter(filter.getFindString().config.getStringValue(), filter.getFind(), filter.getSound(), (float) filter.getSoundVolume().config.getDoubleValue(), (float) filter.getSoundPitch().config.getDoubleValue()));
        }
        if (filter.getReplaceBackgroundColor().config.getBooleanValue()) {
            filters.add(new ColorFilter(filter.getFindString().config.getStringValue(), filter.getFind(), filter.getBackgroundColor().config.getSimpleColor()));
        }
        return filters;
    }
}
