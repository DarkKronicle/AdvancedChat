package io.github.darkkronicle.advancedchat.chat.registry;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchat.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MatchReplaceRegistry extends AbstractRegistry<IMatchReplace, MatchReplaceRegistry.MatchReplaceOption> {

    private MatchReplaceRegistry() {

    }

    private static final MatchReplaceRegistry INSTANCE = new MatchReplaceRegistry();


    public static MatchReplaceRegistry getInstance() {
        return INSTANCE;
    }


    @Override
    public MatchReplaceRegistry clone() {
        MatchReplaceRegistry registry = new MatchReplaceRegistry();
        for (MatchReplaceOption o : getAll()) {
            registry.add(o.copy(registry));
        }
        return registry;
    }

    @Override
    public MatchReplaceOption constructOption(IMatchReplace iMatchReplace, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault) {
        return new MatchReplaceOption(iMatchReplace, saveString, translation, infoTranslation, active, this);
    }

    public static class MatchReplaceOption implements ConfigRegistryOption<IMatchReplace> {

        private final IMatchReplace replace;
        private final String saveString;
        private final String translation;
        private final String infoTranslation;
        private final MatchReplaceRegistry registry;
        private final ConfigStorage.SaveableConfig<ConfigBoolean> active;

        // Only register
        private MatchReplaceOption(IMatchReplace replace, String saveString, String translation, String infoTranslation, boolean active, MatchReplaceRegistry registry) {
            this.replace = replace;
            this.saveString = saveString;
            this.translation = translation;
            this.registry = registry;
            this.infoTranslation = infoTranslation;
            this.active = ConfigStorage.SaveableConfig.fromConfig(saveString, new ConfigBoolean(translation, active, infoTranslation));
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
        public IMatchReplace getOption() {
            return replace;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }

        @Override
        public MatchReplaceOption copy(AbstractRegistry<IMatchReplace, ?> registry) {
            return new MatchReplaceOption(replace, saveString, translation, infoTranslation, isActive(), registry == null ? this.registry : (MatchReplaceRegistry) registry);
        }
    }

}
