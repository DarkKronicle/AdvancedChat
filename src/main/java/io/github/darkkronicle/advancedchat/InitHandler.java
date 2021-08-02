package io.github.darkkronicle.advancedchat;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.darkkronicle.advancedchat.chat.formatters.ColorCodeFormatter;
import io.github.darkkronicle.advancedchat.chat.formatters.CommandColorer;
import io.github.darkkronicle.advancedchat.chat.ChatDispatcher;
import io.github.darkkronicle.advancedchat.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchat.chat.formatters.JSONFormatter;
import io.github.darkkronicle.advancedchat.chat.registry.ChatFormatterRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.ChatSuggestorRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchat.chat.registry.MatchReplaceRegistry;
import io.github.darkkronicle.advancedchat.chat.suggestors.CalculatorSuggestor;
import io.github.darkkronicle.advancedchat.chat.suggestors.ShortcutSuggestor;
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
import io.github.darkkronicle.advancedchat.filters.processors.ForwardProcessor;
import io.github.darkkronicle.advancedchat.filters.processors.NarratorProcessor;
import io.github.darkkronicle.advancedchat.filters.processors.SoundProcessor;
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

        // Initiate match types
        MatchReplaceRegistry matchRegistry = MatchReplaceRegistry.getInstance();
        matchRegistry.register(() -> null, "none", "advancedchat.config.replacetype.none", "advancedchat.config.replacetype.info.none", true, true);
        matchRegistry.register(ChildrenTextReplace::new, "children", "advancedchat.config.replacetype.children", "advancedchat.config.replacetype.info.children", true, false);
        matchRegistry.register(FullMessageTextReplace::new, "fullmessage", "advancedchat.config.replacetype.fullmessage", "advancedchat.config.replacetype.info.fullmessage", true, false);
        matchRegistry.register(OnlyMatchTextReplace::new, "onlymatch", "advancedchat.config.replacetype.onlymatch", "advancedchat.config.replacetype.info.onlymatch", true, false);
        matchRegistry.register(OwOTextReplace::new, "owo", "advancedchat.config.replacetype.owo", "advancedchat.config.replacetype.info.owo", true, false);
        matchRegistry.register(RainbowTextReplace::new, "rainbow", "advancedchat.config.replacetype.rainbow", "advancedchat.config.replacetype.info.rainbow", true, false);
        matchRegistry.register(RomanNumeralTextReplace::new, "romannumeral", "advancedchat.config.replacetype.romannumeral", "advancedchat.config.replacetype.info.romannumeral", true, false);
        matchRegistry.register(ReverseTextReplace::new, "reverse", "advancedchat.config.replacetype.reverse", "advancedchat.config.replacetype.info.reverse", true, false);

        // Initiate processors
        MatchProcessorRegistry processorRegistry = MatchProcessorRegistry.getInstance();
        processorRegistry.register(ChatTabProcessor::new, "chattab", "advancedchat.config.processor.chat", "advancedchat.config.processor.info.chat", false, true, true);
        processorRegistry.register(ForwardProcessor::new, "forward", "advancedchat.config.processor.forward", "advancedchat.config.processor.info.forward", true, false);
        processorRegistry.register(ActionBarProcessor::new, "actionbar", "advancedchat.config.processor.actionbar", "advancedchat.config.processor.info.actionbar", false, false);
        processorRegistry.register(SoundProcessor::new, "sound", "advancedchat.config.processor.sound", "advancedchat.config.processor.info.sound", false, false);
        processorRegistry.register(NarratorProcessor::new, "narrator", "advancedchat.config.processor.narrator", "advancedchat.config.processor.info.narrator", false, false);

        // Initiate chat formattors
        ChatFormatterRegistry chatRegistry = ChatFormatterRegistry.getInstance();
        chatRegistry.register(ColorCodeFormatter::new, "commandcodeformatter", "advancedchat.config.chatformatter.commandcodeformatter", "advancedchat.config.commandcodeformatter.info.commandcolorer", true, false);
        chatRegistry.register(CommandColorer::new, "commandcolorer", "advancedchat.config.chatformatter.commandcolorer", "advancedchat.config.chatformatter.info.commandcolorer", true, true);
        chatRegistry.register(JSONFormatter::new, "jsonformatter", "advancedchat.config.chatformatter.jsonformatter", "advancedchat.config.chatformatter.info.jsonformatter", true, false);

        // Initiate chat suggestors
        ChatSuggestorRegistry suggestorRegistry = ChatSuggestorRegistry.getInstance();
        suggestorRegistry.register(PlayerSuggestor::new, "players", "advancedchat.config.chatsuggestor.players", "advancedchat.config.chatsuggestor.info.players", true, true);
        suggestorRegistry.register(ShortcutSuggestor::new, "shortcuts", "advancedchat.config.chatsuggestor.shortcuts", "advancedchat.config.chatsuggestor.info.shortcuts", true, false);
        suggestorRegistry.register(CalculatorSuggestor::new, "calculator", "advancedchat.config.chatsuggestor.calculator", "advancedchat.config.chatsuggestor.info.calculator", true, false);
        try {
            suggestorRegistry.register(SpellCheckSuggestor.newWithCatch(), "spellcheck", "advancedchat.config.chatsuggestor.spellcheck", "advancedchat.config.chatsuggestor.info.spellcheck", true, false);
        } catch (Exception e) {
            LogManager.getLogger().log(Level.ERROR, "[AdvancedChat] {}", "Couldn't load SpellCheckSuggestor", e);
        }

        // Setup HUD
        AdvancedChatHud.getInstance().reset();
    }

}
