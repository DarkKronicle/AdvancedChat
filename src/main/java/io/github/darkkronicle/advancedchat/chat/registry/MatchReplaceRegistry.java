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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class MatchReplaceRegistry extends AbstractRegistry<IMatchReplace, MatchReplaceRegistry.MatchReplaceOption> {

    public final static String NAME = "matchreplace";

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
    public MatchReplaceOption constructOption(Supplier<IMatchReplace> iMatchReplace, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, boolean setDefault) {
        return new MatchReplaceOption(iMatchReplace, saveString, translation, infoTranslation, active, hidden, this);
    }

    public static class MatchReplaceOption implements ConfigRegistryOption<IMatchReplace> {

        private IMatchReplace replace;
        private final Supplier<IMatchReplace> repl;
        private final String saveString;
        private final String translation;
        private final String infoTranslation;
        private final MatchReplaceRegistry registry;
        private final ConfigStorage.SaveableConfig<ConfigBoolean> active;
        private final boolean hidden;

        // Only register
        private MatchReplaceOption(Supplier<IMatchReplace> replace, String saveString, String translation, String infoTranslation, boolean active, boolean hidden, MatchReplaceRegistry registry) {
            this.repl = replace;
            this.replace = repl.get();
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
        public IMatchReplace getOption() {
            return replace;
        }

        @Override
        public String getSaveString() {
            return saveString;
        }

        @Override
        public MatchReplaceOption copy(AbstractRegistry<IMatchReplace, ?> registry) {
            return new MatchReplaceOption(repl, saveString, translation, infoTranslation, isActive(), isHidden(), registry == null ? this.registry : (MatchReplaceRegistry) registry);
        }
    }

}
