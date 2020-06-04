package darkkronicle.advancedchat.config;

public class ConfigFilter {
    /*
    Class used to store values about filters.
     */

    private String trigger;
    private ReplaceType replaceType;
    private boolean active;
    private boolean ignoreCase;
    private String name;
    private boolean showUnFilterInLog;
    private boolean regex;
    private String replaceTo;
    private NotifyType notifyType;

    public ConfigFilter() {
        trigger = "To Search";
        replaceType = ReplaceType.NONE;
        active = false;
        ignoreCase = false;
        name = "Default";
        showUnFilterInLog = true;
        regex = false;
        replaceTo = "**%REPLACED%**";
        notifyType = NotifyType.NONE;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public ReplaceType getReplaceType() {
        return replaceType;
    }

    public void setReplaceType(ReplaceType replaceType) {
        this.replaceType = replaceType;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowUnFilterInLog() {
        return showUnFilterInLog;
    }

    public void setShowUnFilterInLog(boolean showUnFilterInLog) {
        this.showUnFilterInLog = showUnFilterInLog;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public String getReplaceTo() {
        return replaceTo;
    }

    public NotifyType getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotifyType notifyType) {
        this.notifyType = notifyType;
    }

    public void setReplaceTo(String replaceTo) {
        this.replaceTo = replaceTo;
    }

    public enum ReplaceType {
        NONE,
        ONLYCHANGED,
        FULLLINE
    }

    public enum NotifyType {
        NONE,
        SOUND,
        BANNER
    }
}
