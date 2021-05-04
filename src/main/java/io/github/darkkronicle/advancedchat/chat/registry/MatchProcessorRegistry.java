package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.Translatable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class MatchProcessorRegistry extends AbstractRegistry<IMatchProcessor, MatchProcessorRegistry.MatchProcessorOption> {

    public final static String NAME = "processors";

    private MatchProcessorRegistry() {

    }

    private static final MatchProcessorRegistry INSTANCE = new MatchProcessorRegistry();


    public static MatchProcessorRegistry getInstance() {
        return INSTANCE;
    }

    public int activeAmount() {
        int amount = 0;
        for (MatchProcessorOption option : getAll()) {
            if (option.isActive()) {
                amount++;
            }
        }
        return amount;
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
    public MatchProcessorRegistry clone() {
        MatchProcessorRegistry registry = new MatchProcessorRegistry();
        for (MatchProcessorOption o : getAll()) {
            registry.add(o.copy(registry));
        }
        return registry;
    }

    @Override
    public MatchProcessorOption constructOption(Supplier<IMatchProcessor> iMatchProcessor, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, boolean setDefault) {
        return new MatchProcessorOption(iMatchProcessor, saveString, translation, infoTranslation, active, hidden, this);
    }

    public static class MatchProcessorOption implements Translatable, ConfigRegistryOption<IMatchProcessor> {

        private final Supplier<IMatchProcessor> processor;
        private IMatchProcessor process;
        private final String saveString;
        private final String translation;
        private final MatchProcessorRegistry registry;
        private final String infoTranslation;
        private final ConfigStorage.SaveableConfig<ConfigBoolean> active;
        private final boolean hidden;


        // Only register
        protected MatchProcessorOption(Supplier<IMatchProcessor> processor, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, MatchProcessorRegistry registry) {
            this.processor = processor;
            this.process = processor.get();
            this.saveString = saveString;
            this.translation = translation;
            this.registry = registry;
            this.infoTranslation = infoTranslation;
            this.hidden = hidden;
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
        public boolean isHidden() {
            return hidden;
        }

        @Override
        public String getTranslationKey() {
            return translation;
        }

        @Override
        public IMatchProcessor getOption() {
            return process;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }

        @Override
        public MatchProcessorOption copy(AbstractRegistry<IMatchProcessor, ?> registry) {
            return new MatchProcessorOption(processor, saveString, translation, infoTranslation, isActive(), isHidden(), registry == null ? this.registry : (MatchProcessorRegistry) registry);
        }
    }
}
