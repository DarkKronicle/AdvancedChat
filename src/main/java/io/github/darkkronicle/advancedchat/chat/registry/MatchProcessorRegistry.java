package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.Translatable;

public class MatchProcessorRegistry extends AbstractRegistry<IMatchProcessor, MatchProcessorRegistry.MatchProcessorOption> {

    private MatchProcessorRegistry() {

    }

    private static final MatchProcessorRegistry INSTANCE = new MatchProcessorRegistry();


    public static MatchProcessorRegistry getInstance() {
        return INSTANCE;
    }


    public MatchProcessorOption get(String string) {
        for (MatchProcessorOption option : getAll()) {
            if (option.getStringValue().equals(string)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public MatchProcessorOption constructOption(IMatchProcessor iMatchProcessor, String saveString, String translation, boolean setDefault) {
        return new MatchProcessorOption(iMatchProcessor, saveString, translation, this);
    }

    public static class MatchProcessorOption implements Translatable, IConfigOptionListEntry, RegistryOption<IMatchProcessor> {

        private final IMatchProcessor processor;
        private final String saveString;
        private final String translation;
        private final MatchProcessorRegistry registry;

        // Only register
        private MatchProcessorOption(IMatchProcessor processor, String saveString, String translation, MatchProcessorRegistry registry) {
            this.processor = processor;
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
        public String getTranslationKey() {
            return translation;
        }

        @Override
        public IMatchProcessor getOption() {
            return processor;
        }

        @Override
        public String getSaveString() {
            return null;
        }
    }
}
