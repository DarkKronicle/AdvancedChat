package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MainFilter extends Filter {

    private ReplaceFilter replaceFilter;
    private NotifyFilter notifyFilter;

    public MainFilter() {
        replaceFilter = new ReplaceFilter();
        notifyFilter = new NotifyFilter();
    }

    @Override
    public FilteredMessage filter(String message, ConfigFilter filter) {
        boolean filtered = false;
        FilteredMessage result = null;
        List<FilteredMessage.FilterResult> filters = new ArrayList<>();

        for (ConfigFilter filt : AdvancedChatClient.configObject.configFilters) {
            FilteredMessage mess = null;
            if (filt.getReplaceType() != ConfigFilter.ReplaceType.NONE) {
                mess = replaceFilter.filter(message, filt);
                if (!mess.doesInclude(FilteredMessage.FilterResult.UNKNOWN)) {
                    result = mess;
                    filters.add(mess.getResult()[0]);
                }
                if (!filtered) {
                    filtered = mess.isFiltered();
                }
            }

            FilteredMessage notify;
            if (filt.getNotifyType() != ConfigFilter.NotifyType.NONE) {
                notify = notifyFilter.filter(message, filt);
                if (!notify.doesInclude(FilteredMessage.FilterResult.UNKNOWN)) {
                    filters.add(notify.getResult()[0]);
                }
                if (!filtered) {
                    filtered = notify.isFiltered();
                }
                if (result == null) {
                    result = new FilteredMessage(message, true, false);
                }
            }
        }

        if (filtered) {
            if (filters.size() != 0) {
                result.setResult(filters.toArray(new FilteredMessage.FilterResult[2]));
            }
            return result;
        }
        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.ALLOW);
    }

    @Override
    public void reloadFilters() {

    }

    @Override
    public FilteredMessage.FilterResult filterType() {
        return FilteredMessage.FilterResult.UNKNOWN;
    }
}
