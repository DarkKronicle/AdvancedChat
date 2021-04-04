package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.FluidText;

import java.util.Optional;

/**
 * Base filter class that provides easy way to create new filters.
 */
public abstract class AbstractFilter {

    /**
     * String to be found in filter.
     */
    @Setter @Getter
    protected String filterString;

    /**
     * The {@link Filter.FindType} on how text will be filtered.
     */
    @Setter @Getter
    protected Filter.FindType findType;

    public AbstractFilter() {
        this.filterString = "";
    }

    /**
     * filterSring and findType
     * @param filterString What it searches to find.
     * @param findType How it finds it.
     */
    public AbstractFilter(String filterString, Filter.FindType findType) {
        this.filterString = filterString;
        this.findType = findType;
    }

    /**
     * Called whenever text needed to be filtered comes through. It gets returned
     * in an {@link Optional}. If the optional is empty then it will use the original filtered text.
     * If the optional contains something it will override the old text.
     * @param text Text to be filtered.
     * @return An {@link Optional} that if not empty will override the filtered text.
     */
    public abstract Optional<FluidText> filter(FluidText text);

}
