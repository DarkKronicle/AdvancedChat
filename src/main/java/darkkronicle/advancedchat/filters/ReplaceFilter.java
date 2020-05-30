package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ReplaceFilter extends Filter {

    private List<ConfigFilter> filters = new ArrayList<>();

    @Override
    public void reloadFilters() {
        filters.clear();
        for (ConfigFilter filter : AdvancedChatClient.configObject.configFilters) {
            if (filter.getTriggerFilter() == filterType()) {
                filters.add(filter);
            }
        }
    }

    @Override
    public FilteredMessage filter(String message) {
        if (filters == null || filters.size() == 0) {
            return new FilteredMessage(message, FilteredMessage.FilterResult.UNKNOWN, false, false);
        }
        for (ConfigFilter filter : filters) {
            if (!filter.isActive()) {
                continue;
            }
            if (filter.isIgnoreCase()) {
                if (message.toLowerCase().contains(filter.getTrigger().toLowerCase())) {
                    return new FilteredMessage(message.replaceAll("(?i)"+filter.getTrigger(), "*****"), FilteredMessage.FilterResult.REPLACE, true, filter.isShowUnFilterInLog());
                }
            } else {
                if (message.contains(filter.getTrigger())) {
                    return new FilteredMessage(message.replaceAll(filter.getTrigger(), "*****"), FilteredMessage.FilterResult.REPLACE, true, filter.isShowUnFilterInLog());
                }
            }
        }
        return new FilteredMessage(message, FilteredMessage.FilterResult.UNKNOWN, false, false);
    }

    @Override
    public FilteredMessage.FilterResult filterType() {
        return FilteredMessage.FilterResult.REPLACE;
    }

}
