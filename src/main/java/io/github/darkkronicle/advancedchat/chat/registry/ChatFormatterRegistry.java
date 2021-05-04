package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.IMessageFormatter;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ChatFormatterRegistry extends AbstractRegistry<IMessageFormatter, ChatFormatterRegistry.ChatFormatterOption> {

    private final static ChatFormatterRegistry INSTANCE = new ChatFormatterRegistry();

    public final static String NAME = "formatters";

    public static ChatFormatterRegistry getInstance() {
        return INSTANCE;
    }

    private ChatFormatterRegistry() {

    }

    @Override
    public ChatFormatterRegistry clone() {
        ChatFormatterRegistry registry = new ChatFormatterRegistry();
        for (ChatFormatterOption o : getAll()) {
            registry.add(o.copy(registry));
        }
        return registry;
    }

    @Override
    public ChatFormatterOption constructOption(Supplier<IMessageFormatter> iMessageFormatter, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault, boolean hidden) {
        return new ChatFormatterOption(iMessageFormatter, saveString, translation, infoTranslation, active, hidden, this);
    }

    public static class ChatFormatterOption implements ConfigRegistryOption<IMessageFormatter> {

        private final IMessageFormatter formatter;
        private final String saveString;
        private final String translation;
        private final String infoTranslation;
        private final ChatFormatterRegistry registry;
        private final ConfigStorage.SaveableConfig<ConfigBoolean> active;
        private final boolean hidden;

        private ChatFormatterOption(Supplier<IMessageFormatter> formatter, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, ChatFormatterRegistry registry) {
            this(formatter.get(), saveString, translation, infoTranslation, active, hidden, registry);
        }

        @Override
        public List<String> getHoverLines() {
            return Arrays.asList(StringUtils.translate(infoTranslation).split("\n"));
        }

        // Only register
        private ChatFormatterOption(IMessageFormatter formatter, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, ChatFormatterRegistry registry) {
            this.formatter = formatter;
            this.saveString = saveString;
            this.translation = translation;
            this.registry = registry;
            this.infoTranslation = infoTranslation;
            this.hidden = hidden;
            this.active = ConfigStorage.SaveableConfig.fromConfig("active", new ConfigBoolean(translation, active, infoTranslation));
        }

        @Override
        public boolean isHidden() {
            return hidden;
        }

        @Override
        public ConfigStorage.SaveableConfig<ConfigBoolean> getActive() {
            return active;
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

        @Override
        public ChatFormatterOption copy(AbstractRegistry<IMessageFormatter, ?> registry) {
            return new ChatFormatterOption(formatter, saveString, translation, infoTranslation, isActive(), isHidden(), registry == null ? this.registry : (ChatFormatterRegistry) registry);
        }


    }

}
