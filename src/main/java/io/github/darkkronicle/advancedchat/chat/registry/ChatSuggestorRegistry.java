package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ChatSuggestorRegistry extends AbstractRegistry<IMessageSuggestor, ChatSuggestorRegistry.ChatSuggestorOption> {

    private static final ChatSuggestorRegistry INSTANCE = new ChatSuggestorRegistry();

    public static ChatSuggestorRegistry getInstance() {
        return INSTANCE;
    }

    public final static String NAME = "suggestors";


    @Override
    public ChatSuggestorOption constructOption(Supplier<IMessageSuggestor> iMessageSuggestor, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault) {
        return new ChatSuggestorOption(iMessageSuggestor, saveString, translation, infoTranslation, active, this);
    }

    @Override
    public ChatSuggestorRegistry clone() {
        ChatSuggestorRegistry registry = new ChatSuggestorRegistry();
        for (ChatSuggestorOption o : getAll()) {
            registry.add(o.copy(registry));
        }
        return registry;
    }

    public static class ChatSuggestorOption implements ConfigRegistryOption<IMessageSuggestor> {

        public final String translation;
        public final String saveString;
        private final String infoTranslation;
        private final ChatSuggestorRegistry registry;

        private final IMessageSuggestor suggestor;
        private final ConfigStorage.SaveableConfig<ConfigBoolean> active;

        public ChatSuggestorOption(Supplier<IMessageSuggestor> suggestor, String saveString, String translation, String infoTranslation, boolean active, ChatSuggestorRegistry registry) {
            this(suggestor.get(), saveString, translation, infoTranslation, active, registry);
        }

        public ChatSuggestorOption(IMessageSuggestor suggestor, String saveString, String translation, String infoTranslation, boolean active, ChatSuggestorRegistry registry) {
            this.saveString = saveString;
            this.suggestor = suggestor;
            this.translation = translation;
            this.infoTranslation = infoTranslation;
            this.registry = registry;
            this.active = ConfigStorage.SaveableConfig.fromConfig("active", new ConfigBoolean(translation, active, infoTranslation));
        }

        @Override
        public List<String> getHoverLines() {
            return Arrays.asList(StringUtils.translate(infoTranslation).split("\n"));
        }

        @Override
        public ConfigStorage.SaveableConfig<ConfigBoolean> getActive() {
            return active;
        }

        @Override
        public IMessageSuggestor getOption() {
            return suggestor;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }

        @Override
        public ChatSuggestorOption copy(AbstractRegistry<IMessageSuggestor, ?> registry) {
            return new ChatSuggestorOption(getOption(), saveString, translation, infoTranslation, isActive(), registry == null ? this.registry : (ChatSuggestorRegistry) registry);
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
            return null;
        }
    }
}
