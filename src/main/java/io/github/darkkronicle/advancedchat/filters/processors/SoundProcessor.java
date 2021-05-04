package io.github.darkkronicle.advancedchat.filters.processors;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.gui.SliderCallbackDouble;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetSlider;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.config.gui.GuiConfig;
import io.github.darkkronicle.advancedchat.config.gui.registry.GuiFormatterRegistry;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchat.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SoundProcessor implements IMatchProcessor, IJsonApplier, IScreenSupplier {

    /* How the filter notifies the client of a found string.
    SOUND plays a sound when the filter is triggered.
    */
    private static String translate(String string) {
        return "advancedchat.config.sound." + string;
    }

    private final ConfigStorage.SaveableConfig<ConfigOptionList> notifySound = ConfigStorage.SaveableConfig.fromConfig("notifySound",
            new ConfigOptionList(translate("notifysound"), Filter.NotifySound.NONE, translate("info.notifysound")));

    public Filter.NotifySound getSound() {
        return Filter.NotifySound.fromNotifySoundString(notifySound.config.getStringValue());
    }

    private final ConfigStorage.SaveableConfig<ConfigDouble> soundPitch = ConfigStorage.SaveableConfig.fromConfig("soundPitch",
            new ConfigDouble(translate("soundpitch"), 1, 0.5, 3, translate("info.soundpitch")));

    private final ConfigStorage.SaveableConfig<ConfigDouble> soundVolume = ConfigStorage.SaveableConfig.fromConfig("soundVolume",
            new ConfigDouble(translate("soundvolume"), 1, 0.5, 3, translate("info.soundvolume")));


    @Override
    public Result processMatches(FluidText text, @Nullable FluidText unfiltered, SearchResult search) {
        if (getSound() != Filter.NotifySound.NONE) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(getSound().event, (float) soundPitch.config.getDoubleValue(), (float) soundVolume.config.getDoubleValue()));
            return Result.PROCESSED;
        }
        return Result.FAIL;
    }


    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add(notifySound.key, notifySound.config.getAsJsonElement());
        obj.add(soundPitch.key, soundPitch.config.getAsJsonElement());
        obj.add(soundVolume.key, soundVolume.config.getAsJsonElement());
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        notifySound.config.setValueFromJsonElement(obj.get(notifySound.key));
        soundPitch.config.setValueFromJsonElement(obj.get(soundPitch.key));
        soundVolume.config.setValueFromJsonElement(obj.get(soundVolume.key));
    }


    @Override
    public Supplier<Screen> getScreen(@Nullable Screen parent) {
        return () -> new SoundScreen(parent);
    }

    public class SoundScreen extends GuiBase {

        private final WidgetDropDownList<Filter.NotifySound> widgetDropDown;

        @Override
        public void onClose() {
            save();
            super.onClose();
        }

        public SoundScreen(Screen parent) {
            this.setParent(parent);
            this.widgetDropDown = new WidgetDropDownList<>(0, 0, getWidth(), 20, 200, 10, ImmutableList.copyOf(Filter.NotifySound.values()), Filter.NotifySound::getDisplayName);
            this.widgetDropDown.setZLevel(this.getZOffset() + 100);
            this.setTitle(StringUtils.translate("advancedchat.screen.sound"));
        }

        @Override
        protected void closeGui(boolean showParent) {
            save();
            super.closeGui(showParent);
        }

        public void save() {
            notifySound.config.setOptionListValue(widgetDropDown.getSelectedEntry());
        }

        private int getWidth() {
            return 300;
        }

        @Override
        public void initGui() {
            super.initGui();
            int x = 10;
            int y = 26;

            String name = ButtonListener.Type.BACK.getDisplayName();
            int nameW = StringUtils.getStringWidth(name) + 10;
            ButtonGeneric button = new ButtonGeneric(x, y, nameW, 20, name);
            this.addButton(button, new ButtonListener(ButtonListener.Type.BACK, this));
            y += 30;
            this.addLabel(x + getWidth() / 2, y, soundPitch.config);
            y += this.addLabel(x, y, soundVolume.config) + 1;
            ISliderCallback volumeCallback = new SliderCallbackDouble(soundVolume.config, null);
            this.addWidget(new WidgetSlider(x, y, getWidth() / 2 - 1, 20, volumeCallback));
            ISliderCallback pitchCallback = new SliderCallbackDouble(soundPitch.config, null);
            this.addWidget(new WidgetSlider(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, 20, pitchCallback));
            y += 22;
            // Add this last so it's on top with the drop down
            y += this.addLabel(x, y, notifySound.config) + 1;
            this.widgetDropDown.setPosition(x, y + 1);
            this.widgetDropDown.setSelectedEntry(getSound());
            y += widgetDropDown.getHeight() + 2;
            this.addWidget(this.widgetDropDown);
        }


        private int addLabel(int x, int y, IConfigBase config) {
            int width = StringUtils.getStringWidth(config.getConfigGuiDisplayName());
            WidgetLabelHoverable label = new WidgetLabelHoverable(x, y, width, 8, ColorUtil.WHITE.color(), config.getConfigGuiDisplayName());
            label.setHoverLines(StringUtils.translate(config.getComment()));
            this.addWidget(label);
            return 8;
        }

        public void back() {
            this.closeGui(true);
        }

    }

    public static class ButtonListener implements IButtonActionListener {

        private final SoundScreen parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, SoundScreen parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back"),
            ;
            private final String translation;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }

        }

    }
}
