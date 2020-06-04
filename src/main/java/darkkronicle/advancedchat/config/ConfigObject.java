package darkkronicle.advancedchat.config;

import java.util.List;

public class ConfigObject {
    public List<ConfigFilter> configFilters;
    public int storedLines = 1000;
    public int visibleLines = 100;
    public boolean stackSame = false;
    public boolean linesUpDown = true;
    public boolean clearChat = true;
    public boolean showTime = false;
    public String timeFormat = "hh:mm";
    public String replaceFormat = "&7[%TIME%] ";
}
