package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.minecraft.text.StringRenderable;

import java.util.Optional;

/**
 * <h1>AbstractFilter</h1>
 * Base filter class that provides easy way to create new filters.
 */
public abstract class AbstractFilter {

    /**
     * <h1>FilterString</h1>
     * String to be found in filter.
     */
    @Setter @Getter
    protected String filterString;

    /**
     * <h1>FindType</h1>
     * The {@link net.darkkronicle.advancedchat.storage.Filter.FindType} on how text will be filtered.
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
     * <h1>Filter</h1>
     * Called whenever text needed to be filtered comes through. It gets returned
     * in an {@link Optional}. If the optional is empty then it will use the original filtered text.
     * If the optional contains something it will override the old text.
     * @param text StringRenderable to be filtered.
     * @return An {@link Optional} that if not empty will override the filtered text.
     */
    public abstract Optional<StringRenderable> filter(StringRenderable text);

}
