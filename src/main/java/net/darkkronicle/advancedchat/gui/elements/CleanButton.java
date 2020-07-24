package net.darkkronicle.advancedchat.gui.elements;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;

@EqualsAndHashCode(callSuper = false)
@ToString
@Environment(EnvType.CLIENT)
public class CleanButton extends AbstractButtonWidget {
    private ColorUtil.SimpleColor baseColor;
    @Setter
    private Text text;
    private int x;
    private int y;
    private int width;
    private int height;
    private OnPress onPress;

    private boolean hovered = false;
    private MinecraftClient client = MinecraftClient.getInstance();

    public CleanButton(int x, int y, int width, int height, ColorUtil.SimpleColor baseColor, Text text, OnPress onPress) {
        super(x, y, width, height, text.getString());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.baseColor = baseColor;
        this.text = text;
        this.onPress = onPress;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int relMX = mouseX - x;
        int relMY = mouseY - y;
        hovered = relMX >= 0 && relMX <= width && relMY >= 0 && relMY <= height;
        ColorUtil.SimpleColor color = baseColor;
        if (isHovered()) {
            color = ColorUtil.WHITE.withAlpha(color.alpha());
        }
        fill(x, y, x + width, y + height, color.color());
        drawCenteredString(client.textRenderer, text.getString(), (x + (width / 2)), (y + (height / 2) - 3), ColorUtil.WHITE.color());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered()) {
            onPress.onPress(this);
            return true;
        }
        return false;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {

    }

    public boolean isHovered() {
        return hovered;
    }

    public interface OnPress {
        void onPress(CleanButton button);
    }
}
