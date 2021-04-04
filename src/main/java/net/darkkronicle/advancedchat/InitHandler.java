package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.chat.MessageDispatcher;
import net.darkkronicle.advancedchat.chat.formatters.CommandColorer;
import net.darkkronicle.advancedchat.chat.formatters.JSONFormatter;
import net.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import net.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import net.darkkronicle.advancedchat.chat.registry.MatchReplaceRegistry;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import net.darkkronicle.advancedchat.filters.matchreplace.ReverseTextReplace;
import net.darkkronicle.advancedchat.filters.processors.ActionBarProcessor;
import net.darkkronicle.advancedchat.filters.processors.ChatTabProcessor;
import net.darkkronicle.advancedchat.filters.matchreplace.ChildrenTextReplace;
import net.darkkronicle.advancedchat.filters.matchreplace.FullMessageTextReplace;
import net.darkkronicle.advancedchat.filters.matchreplace.OnlyMatchTextReplace;
import net.darkkronicle.advancedchat.filters.matchreplace.OwOTextReplace;
import net.darkkronicle.advancedchat.filters.matchreplace.RainbowTextReplace;
import net.darkkronicle.advancedchat.filters.matchreplace.RomanNumeralTextReplace;
import net.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(AdvancedChat.MOD_ID, new ConfigStorage());
        RenderEventHandler.getInstance().registerGameOverlayRenderer(AdvancedChatHud.getInstance());
        AdvancedChat.chatTab = new MainChatTab();
        ChatDispatcher.getInstance().setFinalProcessor(new ChatTabProcessor());
        MessageDispatcher.getInstance().register(ChatDispatcher.getInstance(), -1);

        MatchReplaceRegistry matchRegistry = MatchReplaceRegistry.getInstance();
        matchRegistry.register(null, "none", "advancedchat.config.replacetype.none");
        matchRegistry.register(new ChildrenTextReplace(), "children", "advancedchat.config.replacetype.children", true);
        matchRegistry.register(new FullMessageTextReplace(), "fullmessage", "advancedchat.config.replacetype.fullmessage");
        matchRegistry.register(new OnlyMatchTextReplace(), "onlymatch", "advancedchat.config.replacetype.onlymatch");
        matchRegistry.register(new OwOTextReplace(), "owo", "advancedchat.config.replacetype.owo");
        matchRegistry.register(new RainbowTextReplace(), "rainbow", "advancedchat.config.replacetype.rainbow");
        matchRegistry.register(new RomanNumeralTextReplace(), "romannumeral", "advancedchat.config.replacetype.romannumeral");
        matchRegistry.register(new ReverseTextReplace(), "reverse", "advancedchat.config.replacetype.reverse");

        MatchProcessorRegistry processorRegistry = MatchProcessorRegistry.getInstance();
        processorRegistry.register(new ChatTabProcessor(), "chat", "advancedchat.config.processor.chat");
        processorRegistry.register(new ActionBarProcessor(), "actionbar", "advancedchat.config.processor.actionbar");

        ChatFormatterRegistry chatRegistry = ChatFormatterRegistry.getInstance();
        chatRegistry.register(new CommandColorer(), "commandcolorer", "advancedchat.config.formatter.commandcolorer");
        chatRegistry.register(new JSONFormatter(), "jsonformatter", "advancedchat.config.formatter.jsonformatter");
    }

}
