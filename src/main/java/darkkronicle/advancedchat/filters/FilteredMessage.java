package darkkronicle.advancedchat.filters;

public class FilteredMessage {
    private String message;
    private FilterResult result;
    private boolean filtered;

    public FilteredMessage(String message, FilterResult result, boolean filtered) {
        this.message = message;
        this.result = result;
        this.filtered = filtered;
    }

    public String getMessage() {
        return message;
    }

    public FilterResult getResult() {
        return result;
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
        BLOCK,
        REMOVE,
        NOTIFY,
        BANNER,
        UNKNOWN,
        ALLOW
    }
}
