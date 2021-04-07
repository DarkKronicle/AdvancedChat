package io.github.darkkronicle.advancedchat.chat.registry;

import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;

public class ChatSuggestorRegistry extends AbstractRegistry<IMessageSuggestor, ChatSuggestorRegistry.ChatSuggestorOption> {

    private static final ChatSuggestorRegistry INSTANCE = new ChatSuggestorRegistry();

    public static ChatSuggestorRegistry getInstance() {
        return INSTANCE;
    }


    @Override
    public ChatSuggestorOption constructOption(IMessageSuggestor iMessageSuggestor, String saveString, String translation, boolean setDefault) {
        return new ChatSuggestorOption(iMessageSuggestor, saveString, translation);
    }

    public static class ChatSuggestorOption implements RegistryOption<IMessageSuggestor> {

        public final String translation;
        public final String saveString;
        private final IMessageSuggestor suggestor;

        public ChatSuggestorOption(IMessageSuggestor suggestor, String saveString, String translation) {
            this.saveString = saveString;
            this.suggestor = suggestor;
            this.translation = translation;
        }

        @Override
        public IMessageSuggestor getOption() {
            return suggestor;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }
    }
}
