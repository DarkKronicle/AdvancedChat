package io.github.darkkronicle.advancedchat.interfaces;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ConfigRegistryOption<TYPE> extends RegistryOption<TYPE>, IConfigOptionListEntry {
    ConfigStorage.SaveableConfig<ConfigBoolean> getActive();

    @Override
    default boolean isActive() {
        return getActive().config.getBooleanValue();
    }
}
