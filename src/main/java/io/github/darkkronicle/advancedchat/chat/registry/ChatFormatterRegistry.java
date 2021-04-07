package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;

public class ChatFormatterRegistry extends AbstractRegistry<IMessageFormatter, ChatFormatterRegistry.ChatFormatterOption> {

    private final static ChatFormatterRegistry INSTANCE = new ChatFormatterRegistry();

    public static ChatFormatterRegistry getInstance() {
        return INSTANCE;
    }

    private ChatFormatterRegistry() {

    }

    @Override
    public ChatFormatterOption constructOption(IMessageFormatter iMessageFormatter, String saveString, String translation, boolean setDefault) {
        return new ChatFormatterOption(iMessageFormatter, saveString, translation, this);
    }

    public static class ChatFormatterOption implements IConfigOptionListEntry, RegistryOption<IMessageFormatter> {

        private final IMessageFormatter formatter;
        private final String saveString;
        private final String translation;
        private final ChatFormatterRegistry registry;

        // Only register
        private ChatFormatterOption(IMessageFormatter formatter, String saveString, String translation, ChatFormatterRegistry registry) {
            this.formatter = formatter;
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
        public IMessageFormatter getOption() {
            return formatter;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }
    }

}
