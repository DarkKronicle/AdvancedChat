package io.github.darkkronicle.advancedchat.filters.processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.text2speech.Narrator;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchat.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class NarratorProcessor implements IMatchProcessor, IScreenSupplier, IJsonApplier {
    private static String translate(String key) {
        return "advancedchat.config.processor.narrator."  + key;
    }

    private final ConfigStorage.SaveableConfig<ConfigString> message = ConfigStorage.SaveableConfig.fromConfig("message",
            new ConfigString(translate("message"), "$1", translate("info.message")));

    @Override
    public Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
        String content = search.getGroupReplacements(message.config.getStringValue(), true);
        Narrator.getNarrator().say(content, false);
        return Result.getFromBool(true);
    }

    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add(message.key, message.config.getAsJsonElement());
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        message.config.setValueFromJsonElement(obj.get(message.key));
    }

    @Override
    public Supplier<Screen> getScreen(@Nullable Screen parent) {
        return () -> new SenderScreen(parent);
    }

    public class SenderScreen extends GuiBase {

        private GuiTextFieldGeneric textField;

        @Override
        public void onClose() {
            save();
            super.onClose();
        }

        public SenderScreen(Screen parent) {
            this.setParent(parent);
            this.setTitle(StringUtils.translate("advancedchat.screen.narrator"));
        }

        @Override
        protected void closeGui(boolean showParent) {
            save();
            super.closeGui(showParent);
        }

        public void save() {
            message.config.setValueFromString(textField.getText());
        }

        private int getWidth() {
            return 300;
        }

        @Override
        public void initGui() {
            super.initGui();
            int x = 10;
            int y = 26;

            String name = SoundProcessor.ButtonListener.Type.BACK.getDisplayName();
            int nameW = StringUtils.getStringWidth(name) + 10;
            ButtonGeneric button = new ButtonGeneric(x, y, nameW, 20, name);
            this.addButton(button, new ButtonListener(ButtonListener.Type.BACK, this));
            y += 30;
            y += this.addLabel(x, y, message.config) + 1;
            textField = new GuiTextFieldGeneric(x, y, getWidth(), 20, MinecraftClient.getInstance().textRenderer);
            textField.setMaxLength(64000);
            textField.setText(message.config.getStringValue());
            this.addTextField(textField, null);
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

        private final SenderScreen parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, SenderScreen parent) {
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
