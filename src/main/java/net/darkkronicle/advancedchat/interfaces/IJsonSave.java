package net.darkkronicle.advancedchat.interfaces;

import com.google.gson.JsonObject;

public interface IJsonSave<T> {

    T load(JsonObject obj);

    JsonObject save(T t);

}
