package io.github.darkkronicle.advancedchat.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IJsonApplier {
    JsonObject save();

    void load(JsonElement element);
}
