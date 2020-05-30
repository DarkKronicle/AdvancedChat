package darkkronicle.advancedchat.filters;

public abstract class Filter {
    public abstract FilteredMessage filter(String message);

    public abstract FilteredMessage.FilterResult filterType();

    public abstract void reloadFilters();
}
