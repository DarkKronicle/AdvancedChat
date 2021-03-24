package net.darkkronicle.advancedchat.gui.elements;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.render.RenderUtils;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@EqualsAndHashCode(callSuper = false)
@ToString
@Environment(EnvType.CLIENT)
public class CleanButton extends ButtonBase {
    private ColorUtil.SimpleColor baseColor;

    private MinecraftClient client = MinecraftClient.getInstance();

    public CleanButton(int x, int y, int width, int height, ColorUtil.SimpleColor baseColor, String text) {
        super(x, y, width, height, text);
        this.x = x;
        this.y = y;
        this.baseColor = baseColor;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        int relMX = mouseX - x;
        int relMY = mouseY - y;
        hovered = relMX >= 0 && relMX <= width && relMY >= 0 && relMY <= height;
        ColorUtil.SimpleColor color = baseColor;
        if (hovered) {
            color = ColorUtil.WHITE.withAlpha(color.alpha());
        }
        RenderUtils.drawRect(x, y, width, height, color.color());
        drawCenteredString((x + (width / 2)), (y + (height / 2) - 3), ColorUtil.WHITE.color(), displayString, matrixStack);
    }

}
