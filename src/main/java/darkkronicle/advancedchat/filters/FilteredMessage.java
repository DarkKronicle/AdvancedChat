package darkkronicle.advancedchat.filters;

public class FilteredMessage {
    private String message;
    private FilterResult[] result;
    private boolean filtered;
    private boolean showUnfiltered;

    public FilteredMessage(String message, boolean filtered, boolean showUnfiltered, FilterResult... result) {
        this.message = message;
        this.result = result;
        this.filtered = filtered;
        this.showUnfiltered = showUnfiltered;
    }

    public String getMessage() {
        return message;
    }

    public FilterResult[] getResult() {
        return result;
    }

    public boolean isShowUnfiltered() {
        return showUnfiltered;
    }

    public void setResult(FilterResult[] result) {
        this.result = result;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public boolean doesInclude(FilterResult test) {
        for (FilterResult filter : this.result) {
            if (test == filter) {
                return true;
            }
        }
        return false;
    }

    public enum FilterResult {
        REPLACE,
        BLOCK,
        NOTIFY,
        UNREAD,
        BANNER,
        UNKNOWN,
        ALLOW
    }
}
