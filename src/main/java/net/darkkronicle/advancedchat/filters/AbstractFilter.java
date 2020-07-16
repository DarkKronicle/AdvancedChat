package net.darkkronicle.advancedchat.filters;

import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.storage.Filter;
import net.minecraft.text.StringRenderable;

import java.util.Optional;

public abstract class AbstractFilter {

    @Setter @Getter
    protected String filterString;
    @Setter @Getter
    protected Filter.FindType findType;

    public AbstractFilter() {
        this.filterString = "";
    }

    public AbstractFilter(String filterString, Filter.FindType findType) {
        this.filterString = filterString;
        this.findType = findType;
    }

    public abstract Optional<StringRenderable> filter(StringRenderable text);

}
