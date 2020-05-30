package darkkronicle.advancedchat.filters;

public class FilteredMessage {
    private String message;
    private FilterResult result;
    private boolean filtered;
    private boolean showUnfiltered;

    public FilteredMessage(String message, FilterResult result, boolean filtered, boolean showUnfiltered) {
        this.message = message;
        this.result = result;
        this.filtered = filtered;
        this.showUnfiltered = showUnfiltered;
    }

    public String getMessage() {
        return message;
    }

    public FilterResult getResult() {
        return result;
    }

    public boolean isShowUnfiltered() {
        return showUnfiltered;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(FilterResult result) {
        this.result = result;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public enum FilterResult {
        REPLACE,
        REMOVE,
        BLOCK,
        NOTIFY,
        BANNER,
        UNKNOWN,
        ALLOW
    }
}
