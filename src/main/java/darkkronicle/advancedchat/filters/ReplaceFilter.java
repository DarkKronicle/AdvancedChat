package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ReplaceFilter extends Filter {

    @Override
    public void reloadFilters() {

    }

    @Override
    public FilteredMessage filter(String message, ConfigFilter filter) {

        boolean filtered = false;
        if (!filter.isActive()) {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
        }
        if (!filter.isRegex()) {
            if (filter.isIgnoreCase()) {
                if (message.toLowerCase().contains(filter.getTrigger().toLowerCase())) {
                    if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                        message = message.replaceAll("(?i)" + filter.getTrigger(), filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§"));
                    } else {
                        message = filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§");
                    }
                    filtered = true;
                }
            } else {
                if (message.contains(filter.getTrigger())) {
                    if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                        message = message.replaceAll(filter.getTrigger(), filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§"));
                    } else {
                        message = filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§");
                    }
                    filtered = true;
                }
            }
        } else {
            Pattern pattern = Pattern.compile(filter.getTrigger());
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                    message = matcher.replaceAll(filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§"));
                } else {
                    message = filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§");
                }
                filtered = true;
            }
        }
        if (filtered) {
            return new FilteredMessage(message, true, filter.isShowUnFilterInLog(), FilteredMessage.FilterResult.REPLACE);
        } else {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
        }
    }

    @Override
    public FilteredMessage.FilterResult filterType() {
        return FilteredMessage.FilterResult.REPLACE;
    }

}
