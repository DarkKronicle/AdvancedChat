package net.darkkronicle.advancedchat.interfaces;

import fi.dy.masa.malilib.util.StringUtils;

public interface Translatable {

    String getTranslationKey();

    default String translate() {
        return StringUtils.translate(getTranslationKey());
    }

}
