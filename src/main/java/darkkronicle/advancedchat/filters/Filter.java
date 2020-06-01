package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.config.ConfigFilter;

public abstract class Filter {
    public abstract FilteredMessage filter(String message, ConfigFilter filter);

    public abstract FilteredMessage.FilterResult filterType();

    public abstract void reloadFilters();
}
