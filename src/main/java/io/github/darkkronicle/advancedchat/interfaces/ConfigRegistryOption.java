package io.github.darkkronicle.advancedchat.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

/**
 * A {@link RegistryOption} that can be saved and loaded from a JSON file.
 * @param <TYPE>
 */
@Environment(EnvType.CLIENT)
public interface ConfigRegistryOption<TYPE> extends RegistryOption<TYPE>, IConfigOptionListEntry, IJsonApplier {

    /**
     * Get's a configurable boolean for whether or not the option is active.
     * @return Configurable boolean
     */
    ConfigStorage.SaveableConfig<ConfigBoolean> getActive();

    /**
     * Get's if the option is currently active.
     * @return If the option is active
     */
    @Override
    default boolean isActive() {
        return getActive().config.getBooleanValue();
    }

    /**
     * Save's the config option and the object that it is wrapping.
     *
     * By default it will only save if the option is active or not, but if the {@link TYPE} implements
     * {@link IJsonApplier} it will also save/load that object.
     *
     * @return Serialized object
     */
    @Override
    default JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add(getActive().key, getActive().config.getAsJsonElement());
        JsonObject extra = null;
        if (getOption() instanceof IJsonApplier) {
            extra = ((IJsonApplier) getOption()).save();
        }
        if (extra != null) {
            for (Map.Entry<String, JsonElement> e : extra.entrySet()) {
                obj.add(e.getKey(), e.getValue());
            }
        }
        return obj;
    }

    /**
     * Load's the config option and the object that it is wrapping.
     *
     * By default it will only load if the option is active or not, but if the {@link TYPE} implements
     * {@link IJsonApplier} it will also save/load that object.
     */
    @Override
    default void load(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        getActive().config.setValueFromJsonElement(obj.get(getActive().key));
        if (getOption() instanceof IJsonApplier) {
            ((IJsonApplier) getOption()).load(obj);
        }
    }
}
