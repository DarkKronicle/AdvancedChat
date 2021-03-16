package net.darkkronicle.advancedchat.config.options;

import fi.dy.masa.malilib.config.options.ConfigColor;
import net.darkkronicle.advancedchat.util.ColorUtil;

public class ConfigSimpleColor extends ConfigColor {

    private ColorUtil.SimpleColor color;

    public ConfigSimpleColor(String name, ColorUtil.SimpleColor defaultValue, String comment) {
        super(name, defaultValue.getString(), comment);
        this.color = defaultValue;
    }

    @Override
    public void setIntegerValue(int value) {
        super.setIntegerValue(value);
        setSimpleColor();
    }

    private void setSimpleColor() {
        this.color = new ColorUtil.SimpleColor(getIntegerValue());
    }

    public ColorUtil.SimpleColor getSimpleColor() {
        return color;
    }

}
