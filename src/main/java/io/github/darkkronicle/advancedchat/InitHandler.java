package io.github.darkkronicle.advancedchat;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.darkkronicle.advancedchat.chat.formatters.CommandColorer;
import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchat.chat.formatters.JSONFormatter;
import io.github.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.ChatSuggestorRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.MatchReplaceRegistry;
import io.github.darkkronicle.advancedchat.chat.suggestors.CalculatorSuggestor;
import io.github.darkkronicle.advancedchat.chat.suggestors.EmotesSuggestor;
import io.github.darkkronicle.advancedchat.chat.suggestors.PlayerSuggestor;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import io.github.darkkronicle.advancedchat.filters.matchreplace.ReverseTextReplace;
import io.github.darkkronicle.advancedchat.filters.processors.ActionBarProcessor;
import io.github.darkkronicle.advancedchat.filters.processors.ChatTabProcessor;
import io.github.darkkronicle.advancedchat.filters.matchreplace.ChildrenTextReplace;
import io.github.darkkronicle.advancedchat.filters.matchreplace.FullMessageTextReplace;
import io.github.darkkronicle.advancedchat.filters.matchreplace.OnlyMatchTextReplace;
import io.github.darkkronicle.advancedchat.filters.matchreplace.OwOTextReplace;
import io.github.darkkronicle.advancedchat.filters.matchreplace.RainbowTextReplace;
import io.github.darkkronicle.advancedchat.filters.matchreplace.RomanNumeralTextReplace;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
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

        ChatSuggestorRegistry suggestorRegistry = ChatSuggestorRegistry.getInstance();
        suggestorRegistry.register(new PlayerSuggestor(), "players", "advancedchat.config.suggestor.players");
        suggestorRegistry.register(new EmotesSuggestor(), "emotes", "advancedchat.config.suggestor.emotes");
        suggestorRegistry.register(new CalculatorSuggestor(), "calculator", "advancedchat.config.suggestor.calculator");
    }

}
