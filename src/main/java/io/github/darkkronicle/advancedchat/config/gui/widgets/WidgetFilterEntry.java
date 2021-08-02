package io.github.darkkronicle.advancedchat.config.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.config.gui.GuiFilterEditor;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;
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
    private final TextFieldWrapper<WidgetIntBox> num;

    public WidgetFilterEntry(int x, int y, int width, int height, boolean isOdd, Filter filter, int listIndex, WidgetListFilters parent) {
        super(x, y, width, height, filter, listIndex);
        this.parent = parent;
        this.isOdd = isOdd;
        this.hoverLines = filter.getWidgetHoverLines();
        this.filter = filter;

        y += 1;

        int pos = x + width - 2;
        WidgetIntBox num = new WidgetIntBox(pos - 40, y, 40, 20, MinecraftClient.getInstance().textRenderer);
        num.setText(filter.getOrder().toString());
        num.setApply(() -> {
            Integer order = num.getInt();
            if (order == null) {
                order = 0;
            }
            this.filter.setOrder(order);
            if (parent.filter == null) {
                Collections.sort(ConfigStorage.FILTERS);
            } else {
                Collections.sort(parent.filter.getChildren());
            }
            ChatDispatcher.getInstance().loadFilters();
            this.parent.refreshEntries();
        });
        this.num = new TextFieldWrapper<>(num, new ITextFieldListener<WidgetIntBox>() {
            @Override
            public boolean onTextChange(WidgetIntBox textField) {
                return false;
            }

            @Override
            public boolean onGuiClosed(WidgetIntBox textField) {
                Integer order = num.getInt();
                if (order == null) {
                    order = 0;
                }
                filter.setOrder(order);
                Collections.sort(ConfigStorage.FILTERS);
                return false;
            }
        });
        this.parent.addTextField(this.num);
        pos -= num.getWidth() + 2;
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

        this.drawTextFields(mouseX, mouseY, matrixStack);

        super.render(mouseX, mouseY, selected, matrixStack);

        RenderUtils.disableDiffuseLighting();
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
                ChatDispatcher.getInstance().loadFilters();
            } else if (type == Type.ACTIVE) {
                this.parent.filter.getActive().config.setBooleanValue(!this.parent.filter.getActive().config.getBooleanValue());
                ChatDispatcher.getInstance().loadFilters();
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

    @Override
    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers) {
        if (this.num != null && this.num.isFocused()) {
            if (keyCode == KeyCodes.KEY_ENTER) {
                this.num.getTextField().getApply().run();
                return true;
            } else {
                return this.num.onKeyTyped(keyCode, scanCode, modifiers);
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(char charIn, int modifiers) {
        if (this.num != null && this.num.onCharTyped(charIn, modifiers)) {
            return true;
        }

        return super.onCharTypedImpl(charIn, modifiers);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        if (super.onMouseClickedImpl(mouseX, mouseY, mouseButton)) {
            return true;
        }

        boolean ret = false;

        if (this.num != null)
        {
            ret = this.num.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (!this.subWidgets.isEmpty()) {
            for (WidgetBase widget : this.subWidgets) {
                ret |= widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        return ret;
    }

    protected void drawTextFields(int mouseX, int mouseY, MatrixStack matrixStack) {
        if (this.num != null) {
            this.num.getTextField().render(matrixStack, mouseX, mouseY, 0f);
        }
    }

}
