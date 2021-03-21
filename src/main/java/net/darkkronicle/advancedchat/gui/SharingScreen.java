package net.darkkronicle.advancedchat.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SplitText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class SharingScreen extends Screen {

    private final String starting;
    private static final Gson GSON = new GsonBuilder().create();
    private TextFieldWidget text;
    private boolean good = false;
    private String bad = null;

    public SharingScreen() {
        this(null);
    }

    public SharingScreen(String starting) {
        super(new TranslatableText("advancedchat.screen.export"));
        this.starting = starting;
        if (starting != null) {
            System.out.println(starting.length());
        }
    }

    public static SharingScreen fromFilter(Filter filter) {
        return new SharingScreen(GSON.toJson(filter, Filter.class));
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);

        text = new TextFieldWidget(client.textRenderer, this.width / 2 - 150, 50, 300, 20, new TranslatableText("config.advancedchat.screen.export.text"));
        text.setMaxLength(12800);
        if (starting != null) {
            text.setText(starting);
            text.setTextFieldFocused(true);
        }
        text.changeFocus(true);
        text.setDrawsBackground(true);
        text.setEditable(true);
        text.changeFocus(true);
        addButton(text);
        addButton(new ButtonWidget(width / 2 - 50, height - 25, 100, 20, new TranslatableText("config.advancedchat.filter.export"), (button1) -> {
            bad = null;
            good = false;
            try {
                Filter filter = GSON.fromJson(text.getText(), Filter.class);
                if (filter == null) {
                    throw new NullPointerException("Filter is null!");
                }
                ConfigStorage.FILTERS.add(filter);
                good = true;
            } catch (Exception e) {
                bad = e.getMessage();
            }
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (bad != null) {
            client.textRenderer.drawWithShadow(matrices, new SplitText(new TranslatableText("advancedchat.error.filter.export")).getFullMessage() + " " + bad, 10, 10, new ColorUtil.SimpleColor(204, 50, 50, 255).color());
        }
        if (good) {
            client.textRenderer.drawWithShadow(matrices, new SplitText(new TranslatableText("advancedchat.success.filter.export")).getFullMessage(), 10, 10, ColorUtil.WHITE.color());
        }
    }
}
