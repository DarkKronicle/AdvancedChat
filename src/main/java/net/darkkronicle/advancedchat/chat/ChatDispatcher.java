package net.darkkronicle.advancedchat.chat;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.filters.processors.ChatTabProcessor;
import net.darkkronicle.advancedchat.interfaces.IMessageProcessor;
import net.darkkronicle.advancedchat.mixin.MixinChatHudInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 * A hook into {@link MessageDispatcher} for forwarding chat events. This handles the filters and gives the contents
 * of the message to {@link net.minecraft.client.gui.hud.ChatHud}
 */
@Environment(EnvType.CLIENT)
public class ChatDispatcher implements IMessageProcessor {

    private final static ChatDispatcher INSTANCE = new ChatDispatcher();

    /**
     * The "Terminate" text. It has a length of zero and is non-null so it will stop the process if
     * returned by the main filter.
     */
    public final static Text TERMINATE = new LiteralText("");

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
    public boolean process(Text text, Text original) {
        MinecraftClient client = MinecraftClient.getInstance();
        Text unfiltered = text;

        // Filter text
        Optional<Text> filtered = AdvancedChat.filter.filter(text);
        if (filtered.isPresent()) {
            text = filtered.get();
        }
        if (text.getString().length() != 0) {
            new ChatTabProcessor().process(text, unfiltered);
        }
        return true;
    }
}
