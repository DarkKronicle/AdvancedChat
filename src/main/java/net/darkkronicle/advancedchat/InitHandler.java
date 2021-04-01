package net.darkkronicle.advancedchat;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.darkkronicle.advancedchat.chat.ChatDispatcher;
import net.darkkronicle.advancedchat.chat.MessageDispatcher;
import net.darkkronicle.advancedchat.chat.registry.MatchReplaceRegistry;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.chat.tabs.MainChatTab;
import net.darkkronicle.advancedchat.filters.matchreplace.ReverseTextReplace;
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

        MatchReplaceRegistry registry = MatchReplaceRegistry.getInstance();
        registry.register(null, "none", "advancedchat.config.replacetype.none");
        registry.register(new ChildrenTextReplace(), "children", "advancedchat.config.replacetype.children", true);
        registry.register(new FullMessageTextReplace(), "fullmessage", "advancedchat.config.replacetype.fullmessage");
        registry.register(new OnlyMatchTextReplace(), "onlymatch", "advancedchat.config.replacetype.onlymatch");
        registry.register(new OwOTextReplace(), "owo", "advancedchat.config.replacetype.owo");
        registry.register(new RainbowTextReplace(), "rainbow", "advancedchat.config.replacetype.rainbow");
        registry.register(new RomanNumeralTextReplace(), "romannumeral", "advancedchat.config.replacetype.romannumeral");
        registry.register(new ReverseTextReplace(), "reverse", "advancedchat.config.replacetype.reverse");
    }

}
