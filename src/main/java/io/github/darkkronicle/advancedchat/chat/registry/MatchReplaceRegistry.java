package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;

public class MatchReplaceRegistry extends AbstractRegistry<IMatchReplace, MatchReplaceRegistry.MatchReplaceOption> {

    private MatchReplaceRegistry() {

    }

    private static final MatchReplaceRegistry INSTANCE = new MatchReplaceRegistry();


    public static MatchReplaceRegistry getInstance() {
        return INSTANCE;
    }


    @Override
    public MatchReplaceOption constructOption(IMatchReplace iMatchReplace, String saveString, String translation, boolean setDefault) {
        return new MatchReplaceOption(iMatchReplace, saveString, translation, this);
    }

    public static class MatchReplaceOption implements IConfigOptionListEntry, RegistryOption<IMatchReplace> {

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


        @Override
        public IMatchReplace getOption() {
            return replace;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }
    }

}
