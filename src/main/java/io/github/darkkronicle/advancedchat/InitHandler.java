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
import io.github.darkkronicle.advancedchat.chat.suggestors.SpellCheckSuggestor;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

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
        matchRegistry.register(null, "none", "advancedchat.config.replacetype.none", "advancedchat.config.replacetype.info.none", true, true);
        matchRegistry.register(new ChildrenTextReplace(), "children", "advancedchat.config.replacetype.children", "advancedchat.config.replacetype.info.children", true, false);
        matchRegistry.register(new FullMessageTextReplace(), "fullmessage", "advancedchat.config.replacetype.fullmessage", "advancedchat.config.replacetype.info.fullmessage", true, false);
        matchRegistry.register(new OnlyMatchTextReplace(), "onlymatch", "advancedchat.config.replacetype.onlymatch", "advancedchat.config.replacetype.info.onlymatch", true, false);
        matchRegistry.register(new OwOTextReplace(), "owo", "advancedchat.config.replacetype.owo", "advancedchat.config.replacetype.info.owo", true, false);
        matchRegistry.register(new RainbowTextReplace(), "rainbow", "advancedchat.config.replacetype.rainbow", "advancedchat.config.replacetype.info.rainbow", true, false);
        matchRegistry.register(new RomanNumeralTextReplace(), "romannumeral", "advancedchat.config.replacetype.romannumeral", "advancedchat.config.replacetype.info.romannumeral", true, false);
        matchRegistry.register(new ReverseTextReplace(), "reverse", "advancedchat.config.replacetype.reverse", "advancedchat.config.replacetype.info.reverse", true, false);

        MatchProcessorRegistry processorRegistry = MatchProcessorRegistry.getInstance();
        processorRegistry.register(new ChatTabProcessor(), "chat", "advancedchat.config.processor.chat", "advancedchat.config.processor.info.chat", true, true);
        processorRegistry.register(new ActionBarProcessor(), "actionbar", "advancedchat.config.processor.actionbar", "advancedchat.config.processor.actionbar", true, false);

        ChatFormatterRegistry chatRegistry = ChatFormatterRegistry.getInstance();
        chatRegistry.register(new CommandColorer(), "commandcolorer", "advancedchat.config.formatter.commandcolorer", "advancedchat.config.formatter.info.commandcolorer", true, true);
        chatRegistry.register(new JSONFormatter(), "jsonformatter", "advancedchat.config.formatter.jsonformatter", "advancedchat.config.formatter.info.jsonformatter", true, false);

        ChatSuggestorRegistry suggestorRegistry = ChatSuggestorRegistry.getInstance();
        suggestorRegistry.register(new PlayerSuggestor(), "players", "advancedchat.config.suggestor.players", "advancedchat.config.suggestor.info.players", true, true);
        suggestorRegistry.register(new EmotesSuggestor(), "emotes", "advancedchat.config.suggestor.emotes", "advancedchat.config.suggestor.info.emotes", true, false);
        suggestorRegistry.register(new CalculatorSuggestor(), "calculator", "advancedchat.config.suggestor.calculator", "advancedchat.config.suggestor.info.calculator", true, false);
        try {
            suggestorRegistry.register(new SpellCheckSuggestor(), "spellcheck", "advancedchat.config.suggestor.spellcheck", "advancedchat.config.suggestor.info.spellcheck", true, false);
        } catch (Exception e) {
            LogManager.getLogger().log(Level.ERROR, "[AdvancedChat] {}", "Couldn't load SpellCheckSuggestor", e);
        }
    }

}
