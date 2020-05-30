package darkkronicle.advancedchat.filters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class MainFilter extends Filter {

    private ArrayList<Filter> filters = new ArrayList<>();
    private FilteredMessage.FilterResult toFilter;

    public MainFilter() {
        filters.add(new BlockedFilter());
        filters.add(new ReplaceFilter());
        filters.add(new RemoveFilter());
    }

    @Override
    public FilteredMessage filter(String message) {
        boolean filtered = false;
        int i = 0;
        FilteredMessage result = null;
        while (i < filters.size()) {
            FilteredMessage mess  = filters.get(i).filter(message);
            if(mess.isFiltered()) {
                filtered = true;
                result = mess;
                message = result.getMessage();
            }
            i++;
        }
        if (filtered) {
            return result;
        }
        return new FilteredMessage(message, FilteredMessage.FilterResult.ALLOW, true, false);
    }

    @Override
    public void reloadFilters() {
        for (Filter filter : filters) {
            filter.reloadFilters();
        }
    }

    @Override
    public FilteredMessage.FilterResult filterType() {
        return FilteredMessage.FilterResult.UNKNOWN;
    }
}
