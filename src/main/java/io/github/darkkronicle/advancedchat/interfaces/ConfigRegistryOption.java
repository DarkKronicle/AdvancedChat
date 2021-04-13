package io.github.darkkronicle.advancedchat.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public interface ConfigRegistryOption<TYPE> extends RegistryOption<TYPE>, IConfigOptionListEntry, IJsonApplier {
    ConfigStorage.SaveableConfig<ConfigBoolean> getActive();

    @Override
    default boolean isActive() {
        return getActive().config.getBooleanValue();
    }

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
