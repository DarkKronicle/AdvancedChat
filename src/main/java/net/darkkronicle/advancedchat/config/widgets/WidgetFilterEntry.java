package net.darkkronicle.advancedchat.config.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.ConfigStorage;
import net.darkkronicle.advancedchat.config.GuiFilterEditor;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.function.Consumer;

/*
    This class is based heavily off of https://github.com/maruohon/minihud/blob/d565d39c68bdcd3ed1e1cf2007491e03d9659f34/src/main/java/fi/dy/masa/minihud/gui/widgets/WidgetShapeEntry.java#L19 which is off the GNU LGPL

 */
@Environment(EnvType.CLIENT)
public class WidgetFilterEntry extends WidgetListEntryBase<Filter> {

    private final WidgetListFilters parent;
    private final boolean isOdd;
    private final List<String> hoverLines;
    private final int buttonStartX;
    private final Filter filter;

    public WidgetFilterEntry(int x, int y, int width, int height, boolean isOdd, Filter filter, int listIndex, WidgetListFilters parent) {
        super(x, y, width, height, filter, listIndex);
        this.parent = parent;
        this.isOdd = isOdd;
        this.hoverLines = filter.getWidgetHoverLines();
        this.filter = filter;

        y += 1;

        int pos = x + width - 2;
        if (parent.filter != null) {
            pos -= addButton(pos, y, ButtonListener.Type.REMOVE, (filt) -> parent.filter.getChildren().remove(filt));
        } else {
            pos -= addButton(pos, y, ButtonListener.Type.REMOVE, ConfigStorage.FILTERS::remove);
        }
        pos -= addOnOffButton(pos, y, ButtonListener.Type.ACTIVE, filter.getActive().config.getBooleanValue());
        pos -= addButton(pos, y, ButtonListener.Type.CONFIGURE, null);

        buttonStartX = pos;
    }

    protected int addButton(int x, int y, ButtonListener.Type type, Consumer<Filter> remove) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this, remove));

        return button.getWidth() + 1;
    }

    private int addOnOffButton(int xRight, int y, ButtonListener.Type type, boolean isCurrentlyOn) {
        ButtonOnOff button = new ButtonOnOff(xRight, y, -1, true, type.translate, isCurrentlyOn);
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth() + 1;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(150).color());
        } else if (this.isOdd) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(70).color());
        } else {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, ColorUtil.WHITE.withAlpha(50).color());
        }
        String name = this.filter.getName().config.getStringValue();
        this.drawString(this.x + 4, this.y + 7, ColorUtil.WHITE.color(), name, matrixStack);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        super.render(mouseX, mouseY, selected, matrixStack);

        RenderUtils.disableDiffuseLighting();
        RenderSystem.disableLighting();
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        super.postRenderHovered(mouseX, mouseY, selected, matrixStack);

        if (mouseX >= this.x && mouseX < this.buttonStartX && mouseY >= this.y && mouseY <= this.y + this.height) {
            RenderUtils.drawHoverText(mouseX, mouseY, this.hoverLines, matrixStack);
        }
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final WidgetFilterEntry parent;
        private final Consumer<Filter> remove;

        public ButtonListener(Type type, WidgetFilterEntry parent) {
            this(type, parent, null);
        }

        public ButtonListener(Type type, WidgetFilterEntry parent, Consumer<Filter> remove) {
            this.parent = parent;
            this.type = type;
            this.remove = remove;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (type == Type.REMOVE) {
                if (remove != null) {
                    remove.accept(parent.filter);
                }
                parent.parent.refreshEntries();
                AdvancedChat.filter.loadFilters();
            } else if (type == Type.ACTIVE) {
                this.parent.filter.getActive().config.setBooleanValue(!this.parent.filter.getActive().config.getBooleanValue());
                AdvancedChat.filter.loadFilters();
                parent.parent.refreshEntries();
            } else if (type == Type.CONFIGURE) {
                GuiBase.openGui(new GuiFilterEditor(parent.filter, parent.parent.getParent()));
            }
        }

        public enum Type {
            CONFIGURE("configure"),
            REMOVE("remove"),
            ACTIVE("active")
            ;

            private final String translate;

            Type(String name) {
                this.translate = translate(name);
            }

            private static String translate(String key) {
                return "advancedchat.config.filtermenu." + key;
            }

            public String getDisplayName() {
                return StringUtils.translate(translate);
            }

        }

    }

}
