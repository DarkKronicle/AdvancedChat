package net.darkkronicle.advancedchat.chat.registry;

import lombok.Getter;
import lombok.NonNull;
import net.darkkronicle.advancedchat.interfaces.RegistryOption;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRegistry<TYPE, OPTION extends RegistryOption<TYPE>> {

    private ArrayList<OPTION> options = new ArrayList<>();

    public List<OPTION> getAll() {
        return options;
    }

    @Getter
    private OPTION defaultOption;

    public void register(TYPE replace, String saveString, String translation) {
        register(replace, saveString, translation, false);
    }

    public void register(TYPE replace, String saveString, String translation, boolean setDefault) {
        OPTION option = constructOption(replace, saveString, translation, setDefault);
        options.add(option);
        if (setDefault) {
            defaultOption = option;
        } else if (defaultOption == null) {
            // If none get set, set it.
            defaultOption = option;
        }
    }

    public abstract OPTION constructOption(TYPE type, String saveString, String translation, boolean setDefault);

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
