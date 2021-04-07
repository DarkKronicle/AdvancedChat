package io.github.darkkronicle.advancedchat.config.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.gui.SliderCallbackDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetSlider;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetToggle;
import io.github.darkkronicle.advancedchat.gui.SharingScreen;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;


public class GuiFilterEditor extends GuiBase {

    public final Filter filter;

    private GuiTextFieldGeneric name;
    private GuiTextFieldGeneric findString;
    private GuiTextFieldGeneric replaceString;
    private WidgetToggle setTextColor;
    private WidgetColor textColor;
    private WidgetToggle setBackgroundColor;
    private WidgetColor backgroundColor;
    private WidgetDropDownList<Filter.NotifySound> widgetDropDown;
    private OnOffListListener<MatchProcessorRegistry.MatchProcessorOption> processors;

    public FilterTab tab = FilterTab.CONFIG;

    public GuiFilterEditor(Filter filter, Screen parent) {
        this.filter = filter;
        this.title = filter.getName().config.getStringValue();
        this.setParent(parent);
        this.widgetDropDown = new WidgetDropDownList<>(0, 0, getWidth(), 20, 200, 10, ImmutableList.copyOf(Filter.NotifySound.values()), Filter.NotifySound::getDisplayName);
        this.widgetDropDown.setZLevel(this.getZOffset() + 100);
    }

    @Override
    protected void closeGui(boolean showParent) {
        // Save the changes :)
        save();
        super.closeGui(showParent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;
        for (FilterTab tab : FilterTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
            }

            x += this.createButton(x, y, width, tab);
        }
        x = 10;
        y += 24;
        createButtons(x, y);

    }

    private int createButton(int x, int y, int width, FilterTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.tab != tab);
        this.addButton(button, new ButtonListenerFilterTabs(tab, this));

        return button.getWidth() + 2;
    }

    private int getWidth() {
        return 300;
    }

    public void save() {
        filter.getName().config.setValueFromString(name.getText());
        filter.getFindString().config.setValueFromString(findString.getText());
        filter.getReplaceTo().config.setValueFromString(replaceString.getText());
        filter.getTextColor().config.setIntegerValue(textColor.getAndRefreshColor().color());
        filter.getReplaceTextColor().config.setBooleanValue(setTextColor.isCurrentlyOn());
        filter.getBackgroundColor().config.setIntegerValue(backgroundColor.getAndRefreshColor().color());
        filter.getReplaceBackgroundColor().config.setBooleanValue(setBackgroundColor.isCurrentlyOn());
        filter.getNotifySound().config.setOptionListValue(widgetDropDown.getSelectedEntry());
        ArrayList<IMatchProcessor> newProcessors = new ArrayList<>();
        for (MatchProcessorRegistry.MatchProcessorOption option : processors.getOn()) {
            newProcessors.add(option.getOption());
        }
        filter.setProcessors(newProcessors);
        ChatDispatcher.getInstance().loadFilters();
    }

    private void createButtons(int x, int y) {
        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(back, new ButtonListener(ButtonListener.Type.BACK, this));
        int topx = x;
        topx += back.getWidth() + 2;

        String exportText = ButtonListener.Type.EXPORT.getDisplayName();
        int exportWidth = StringUtils.getStringWidth(exportText) + 10;
        ButtonGeneric export = new ButtonGeneric(topx + exportWidth, y, exportWidth, true, exportText);
        this.addButton(export, new ButtonListener(ButtonListener.Type.EXPORT, this));
        y += back.getHeight() + 2;
        y += this.addLabel(x, y, filter.getName().config) + 1;
        name = this.addStringConfigButton(x, y, getWidth(), 10, filter.getName().config);
        y += name.getHeight() + 4;

        // Find
        this.addLabel(x + getWidth() / 2, y, filter.getFindType().config);
        y += this.addLabel(x, y, filter.getFindString().config) + 1;
        findString = this.addStringConfigButton(x, y, getWidth() / 2 - 1, 18, filter.getFindString().config);
        ConfigButtonOptionList findType = new ConfigButtonOptionList(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, 20, filter.getFindType().config);
        this.addButton(findType, null);
        y += findType.getHeight() + 2;

        // Replace
        this.addLabel(x + getWidth() / 2, y, filter.getReplaceType().config);
        y += this.addLabel(x, y, filter.getReplaceTo().config) + 1;
        replaceString = this.addStringConfigButton(x, y, getWidth() / 2 - 1, 18, filter.getReplaceTo().config);
        ConfigButtonOptionList replaceType = new ConfigButtonOptionList(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, 20, filter.getReplaceType().config);
        this.addButton(replaceType, null);
        y += findType.getHeight() + 2;

        ConfigOptionList processorOptions = new ConfigOptionList("advancedchat.config.filter.processor", MatchProcessorRegistry.getInstance().getDefaultOption(), "advancedchat.config.filter.info.processor");
        this.addLabel(x + getWidth() / 2 + 1, y, "advancedchat.config.filter.processortoggle", "advancedchat.config.filter.info.processortoggle");
        y += this.addLabel(x, y, processorOptions) + 1;
        WidgetToggle onOff = new WidgetToggle(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, false, "advancedchat.config.filter.processoron", filter.getProcessors().contains(MatchProcessorRegistry.getInstance().getDefaultOption().getOption()));
        ButtonGeneric processorType = new ButtonGeneric(x, y, getWidth() / 2 - 1, 20, MatchProcessorRegistry.getInstance().getDefaultOption().translate());

        HashMap<MatchProcessorRegistry.MatchProcessorOption, Boolean> on = new HashMap<>();
        for (MatchProcessorRegistry.MatchProcessorOption option : MatchProcessorRegistry.getInstance().getAll()) {
            on.put(option, filter.getProcessors().contains(option.getOption()));
        }

        processors = new OnOffListListener<>(processorType, onOff, on);
        this.addButton(processorType, processors);
        this.addButton(onOff, processors.getButtonListener());
        y += findType.getHeight() + 2;

        // Text color
        this.addLabel(x, y, filter.getTextColor().config);
        y += this.addLabel(x + getWidth() / 2, y, filter.getReplaceTextColor().config) + 1;
        textColor = new WidgetColor(x, y, getWidth() / 2 - 1, 18, filter.getTextColor().config.getSimpleColor(), textRenderer);
        this.addTextField(textColor, null);
        setTextColor = new WidgetToggle(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, false, "advancedchat.config.filter.textcoloractive", filter.getReplaceTextColor().config.getBooleanValue());
        this.addButton(setTextColor, null);
        y += findType.getHeight() + 2;

        // Background color
        this.addLabel(x, y, filter.getBackgroundColor().config);
        y += this.addLabel(x + getWidth() / 2, y, filter.getReplaceBackgroundColor().config) + 1;
        backgroundColor = new WidgetColor(x, y, getWidth() / 2 - 1, 18, filter.getBackgroundColor().config.getSimpleColor(), textRenderer);
        this.addTextField(backgroundColor, null);
        setBackgroundColor = new WidgetToggle(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, false, "advancedchat.config.filter.backgroundcoloractive", filter.getReplaceBackgroundColor().config.getBooleanValue());
        this.addButton(setBackgroundColor, null);
        y += findType.getHeight() + 2;
        this.addLabel(x + getWidth() / 2, y, filter.getSoundPitch().config);
        y += this.addLabel(x, y, filter.getSoundVolume().config) + 1;
        ISliderCallback volumeCallback = new SliderCallbackDouble(filter.getSoundVolume().config, null);
        this.addWidget(new WidgetSlider(x, y, getWidth() / 2 - 1, 20, volumeCallback));
        ISliderCallback pitchCallback = new SliderCallbackDouble(filter.getSoundPitch().config, null);
        this.addWidget(new WidgetSlider(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, 20, pitchCallback));
        y += 22;
        // Add this last so it's on top with the drop down
        y += this.addLabel(x, y, filter.getNotifySound().config) + 1;
        this.widgetDropDown.setPosition(x, y + 1);
        this.widgetDropDown.setSelectedEntry(filter.getSound());
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

    private int addLabel(int x, int y, String nameTranslation, String hoverTranslation) {
        String display = StringUtils.translate(nameTranslation);
        int width = StringUtils.getStringWidth(display);
        WidgetLabelHoverable label = new WidgetLabelHoverable(x, y, width, 8, ColorUtil.WHITE.color(), display);
        label.setHoverLines(StringUtils.translate(hoverTranslation));
        this.addWidget(label);
        return 8;
    }

    private GuiTextFieldGeneric addStringConfigButton(int x, int y, int width, int height, ConfigString conf) {
        GuiTextFieldGeneric name = new GuiTextFieldGeneric(x, y, width, height, this.textRenderer);
        name.setMaxLength(128);
        name.setText(conf.getStringValue());
        this.addTextField(name, null);
        return name;
    }

    public void back() {
        closeGui(true);
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiFilterEditor parent;
        private final Type type;

        public ButtonListener(Type type, GuiFilterEditor parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.BACK) {
                parent.back();
            } else if (this.type == Type.EXPORT) {
                parent.save();
                GuiBase.openGui(SharingScreen.fromFilter(parent.filter, parent));
            }
        }

        public enum Type {
            BACK("back"),
            EXPORT("export")
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

    public enum FilterTab {
        CONFIG("config"),
        CHILDREN("children")
        ;

        private final String translation;

        private static String translate(String key) {
            return "advancedchat.config.filter.editor." + key;
        }

        FilterTab(String key) {
            this.translation = translate(key);
        }

        public String getDisplayName() {
            return StringUtils.translate(translation);
        }
    }

    public static class ButtonListenerFilterTabs implements IButtonActionListener {

        private final FilterTab tab;
        private final GuiFilterEditor parent;

        public ButtonListenerFilterTabs(FilterTab type, GuiFilterEditor parent) {
            this.parent = parent;
            this.tab = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            parent.tab = tab;
            if (tab == FilterTab.CONFIG) {
                GuiBase.openGui(new GuiFilterEditor(parent.filter, parent.getParent()));
            } else if (tab == FilterTab.CHILDREN) {
                GuiBase.openGui(new GuiChildrenManager(this.parent));
            }
        }
    }

}
