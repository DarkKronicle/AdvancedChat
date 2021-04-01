package net.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import lombok.Getter;
import net.darkkronicle.advancedchat.interfaces.IMatchReplace;

import java.util.ArrayList;

public class MatchReplaceRegistry {

    private static final MatchReplaceRegistry INSTANCE = new MatchReplaceRegistry();

    private ArrayList<MatchReplaceOption> options = new ArrayList<>();

    private MatchReplaceOption defaultReplace;

    public static MatchReplaceRegistry getInstance() {
        return INSTANCE;
    }

    public MatchReplaceOption getNext(MatchReplaceOption option, boolean forward) {
        if (options.size() == 0) {
            return null;
        }
        int i = options.indexOf(option);
        if (i < 0) {
            return options.get(0);
        }
        if (forward) {
            i = i + 1;
            if (i >= options.size()) {
                return options.get(0);
            }
        } else {
            i = i - 1;
            if (i < 0) {
                return options.get(options.size() - 1);
            }
        }
        return options.get(i);
    }

    public MatchReplaceOption fromString(String string) {
        for (MatchReplaceOption m : options) {
            if (m.saveString.equals(string)) {
                return m;
            }
        }
        return defaultReplace;
    }

    public void register(IMatchReplace replace, String saveString, String translation) {
        register(replace, saveString, translation, false);
    }

    public void register(IMatchReplace replace, String saveString, String translation, boolean setDefault) {
        MatchReplaceOption option = new MatchReplaceOption(replace, saveString, translation, this);
        options.add(option);
        if (setDefault) {
            defaultReplace = option;
        } else if (defaultReplace == null) {
            // If none get set, set it.
            defaultReplace = option;
        }
    }

    public MatchReplaceOption getDefaultReplace() {
        return defaultReplace;
    }

    public static class MatchReplaceOption implements IConfigOptionListEntry {

        @Getter
        private final IMatchReplace replace;
        private final String saveString;
        private final String translation;
        private final MatchReplaceRegistry registry;

        // Only register
        private MatchReplaceOption(IMatchReplace replace, String saveString, String translation, MatchReplaceRegistry registry) {
            this.replace = replace;
            this.saveString = saveString;
            this.translation = translation;
            this.registry = registry;
        }

        @Override
        public String getStringValue() {
            return saveString;
        }

        @Override
        public String getDisplayName() {
            return StringUtils.translate(translation);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            return registry.getNext(this, forward);
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return registry.fromString(value);
        }


    }

}
