package io.github.darkkronicle.advancedchat.chat.registry;

import io.github.darkkronicle.advancedchat.interfaces.RegistryOption;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractRegistry<TYPE, OPTION extends RegistryOption<TYPE>> {

    private List<OPTION> options = new ArrayList<>();

    public List<OPTION> getAll() {
        return options;
    }

    @Getter
    private OPTION defaultOption;

    protected void add(OPTION option) {
        if (defaultOption == null) {
            defaultOption = option;
        }
        options.add(option);
    }

    public void register(Supplier<TYPE> replace, String saveString, String translation, String infoTranslation) {
        register(replace, saveString, translation, infoTranslation, true, false);
    }

    public void register(Supplier<TYPE> replace, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault) {
        register(replace, saveString, translation, infoTranslation, active, setDefault, false);
    }

    public void register(Supplier<TYPE> replace, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault, boolean hidden) {
        OPTION option = constructOption(replace, saveString, translation, infoTranslation, active, setDefault, hidden);
        options.add(option);
        if (setDefault || defaultOption == null) {
            defaultOption = option;
        }
    }

    public abstract AbstractRegistry<TYPE, OPTION> clone();

    public abstract OPTION constructOption(Supplier<TYPE> type, String saveString, String translation, String infoTranslation, boolean active, boolean setDefault, boolean hidden);

    public void setDefaultOption(@NonNull OPTION newDefault) {
        defaultOption = newDefault;
    }

    public OPTION fromString(String string) {
        for (OPTION m : options) {
            if (m.getSaveString().equals(string)) {
                return m;
            }
        }
        return defaultOption;
    }

    public OPTION getNext(OPTION option, boolean forward) {
        if (options.size() == 0) {
            return null;
        }
        int i = options.indexOf(option);
        if (i < 0) {
            return options.get(0);
        }
        if (forward) {
            i = i + 1;
            if (i >= options.size()) {
                return options.get(0);
            }
        } else {
            i = i - 1;
            if (i < 0) {
                return options.get(options.size() - 1);
            }
        }
        return options.get(i);
    }

}
