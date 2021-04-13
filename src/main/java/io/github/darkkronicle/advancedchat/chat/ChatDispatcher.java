package io.github.darkkronicle.advancedchat.chat;

import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.filters.ColorFilter;
import io.github.darkkronicle.advancedchat.filters.ParentFilter;
import io.github.darkkronicle.advancedchat.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchat.interfaces.IFilter;
import io.github.darkkronicle.advancedchat.mixin.MixinChatHudInvoker;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import io.github.darkkronicle.advancedchat.util.FluidText;
import lombok.Getter;
import io.github.darkkronicle.advancedchat.filters.ForwardFilter;
import io.github.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A hook into {@link MessageDispatcher} for forwarding chat events. This handles the filters
 */
@Environment(EnvType.CLIENT)
public class ChatDispatcher implements IMessageProcessor {

    @Getter
    private ArrayList<ColorFilter> colorFilters = new ArrayList<>();

    private ArrayList<ParentFilter> filters = new ArrayList<>();

    private final static ChatDispatcher INSTANCE = new ChatDispatcher();

    /**
     * The "Terminate" text. It has a length of zero and is non-null so it will stop the process if
     * returned by the main filter.
     */
    public final static FluidText TERMINATE = new FluidText();

    /**
     * The final processor to go to if the process hasn't been terminated.
     *
     * This is typically used to send the filtered contents back into chat.
     */
    private IMessageProcessor finalProcessor;

    public static ChatDispatcher getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the processor for when the process hasn't been terminated. Is typically used for sending the message
     * back to chat if it hasn't been terminated.
     *
     * @param processor Processor to accept the text data.
     */
    public void setFinalProcessor(IMessageProcessor processor) {
        this.finalProcessor = processor;
    }

    private ChatDispatcher() {
        setFinalProcessor((text, original) -> {
            ((MixinChatHudInvoker) MinecraftClient.getInstance().inGameHud.getChatHud()).invokeAddMessage(text, 0, MinecraftClient.getInstance().inGameHud.getTicks(), false);
            return true;
        });
    }

    @Override
    public boolean process(FluidText text, FluidText original) {
        FluidText unfiltered = text;

        ColorUtil.SimpleColor backgroundColor = null;
        // Filter text
        for (ParentFilter filter : filters) {
            ParentFilter.FilterResult result = filter.filter(text, unfiltered);
            if (result.getColor().isPresent()) {
                backgroundColor = result.getColor().get();
            }
            if (result.getText().isPresent()) {
                text = result.getText().get();
            }
        }
        text.setBackgroundColor(backgroundColor);

        if (text.getString().length() != 0) {
            finalProcessor.process(text, unfiltered);
        }
        return true;
    }

    /**
     * Loads filters that are stored in ConfigStorage.
     */
    public void loadFilters() {
        filters = new ArrayList<>();
        colorFilters = new ArrayList<>();
        for (Filter filter : ConfigStorage.FILTERS) {
            // If it replaces anything.
            ParentFilter filt = createFilter(filter);
            if (filt != null) {
                filters.add(filt);
            }
        }
    }

    public static ParentFilter createFilter(Filter filter) {
        if (!filter.getActive().config.getBooleanValue()) {
            return null;
        }
        ParentFilter filt = new ParentFilter(filter.getFind(), filter.getFindString().config.getStringValue());
        if (filter.getReplace() != null) {
            if (filter.getReplace().useChildren()) {
                ReplaceFilter f = new ReplaceFilter(filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getReplace(), null);
                if (filter.getChildren() != null) {
                    for (Filter child : filter.getChildren()) {
                        ParentFilter childf = createFilter(child);
                        if (childf != null) {
                            for (IFilter childfilter : childf.getFilters()) {
                                f.addChild(childfilter);
                            }
                        }
                    }
                }
                filt.addFilter(f);
            } else if (filter.getReplaceTextColor().config.getBooleanValue()) {
                filt.addFilter(new ReplaceFilter(filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getReplace(), filter.getTextColor().config.getSimpleColor()));
            } else {
                filt.addFilter(new ReplaceFilter(filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"), filter.getReplace(), null));
            }
        }
        if (filter.getReplaceBackgroundColor().config.getBooleanValue()) {
            filt.addFilter(new ColorFilter(filter.getBackgroundColor().config.getSimpleColor()));
        }
        if (filter.getProcessors().activeAmount() > 0) {
            if (filter.getProcessors().activeAmount() == 1) {
                // If it's only the default, don't do anything
                if (!filter.getProcessors().getDefaultOption().isActive()) {
                    filt.addFilter(new ForwardFilter(filter.getProcessors()));
                }
            } else {
                filt.addForwardFilter(new ForwardFilter(filter.getProcessors()));
            }
        }
        return filt;
    }
}
