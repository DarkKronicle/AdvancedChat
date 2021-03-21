package net.darkkronicle.advancedchat.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;
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

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        {
            try {
                if (element.isJsonPrimitive()) {
                    this.value = this.getClampedValue(StringUtils.getColor(element.getAsString(), 0));
                    this.setIntegerValue(value);
                } else {
                    MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
                }
            } catch (Exception e) {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
            }
        }
    }

    public ColorUtil.SimpleColor getSimpleColor() {
        return color;
    }

}
