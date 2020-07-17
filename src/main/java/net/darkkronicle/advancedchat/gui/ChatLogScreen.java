package net.darkkronicle.advancedchat.gui;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ChatLogScreen extends Screen {

    private MinecraftClient client;

    private AbstractChatTab tab = null;

    private int scrolledLines = 0;

    private TextFieldWidget searchBox;
    private String searchText;

    private Filter.FindType findType = Filter.FindType.LITERAL;

    public ChatLogScreen() {
        super(new TranslatableText("screen.advancedchat.chatlog"));
    }

    public static int getWidth() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledWidth() - 40;
    }

    @Override
    public void init() {
        client = MinecraftClient.getInstance();
        String tabname = "All";
        if (tab != null) {
            tabname = tab.getName();
        }
        ButtonWidget tabButton = new ButtonWidget(client.getWindow().getScaledWidth()-60, 10, 50, 20, new LiteralText(tabname), button -> {
            ArrayList<AbstractChatTab> tabs = AdvancedChat.chatTab.getAllChatTabs();
            if (tabs.size() <= 0) {
                return;
            }
            if (tab == null) {
                tab = tabs.get(0);
                button.setMessage(new LiteralText(tab.getName()));
                return;
            }
            int cur = tabs.indexOf(tab) + 1;
            if (cur >= tabs.size()) {
                tab = null;
                button.setMessage(new LiteralText("All"));
                return;
            }
            AbstractChatTab newtab = tabs.get(cur);
            tab = newtab;
            button.setMessage(new LiteralText(tab.getName()));
        });

        searchText = "";
        searchBox = new TextFieldWidget(client.textRenderer, (client.getWindow().getScaledWidth() / 2) - 50, 30, 100, 20, new LiteralText("Search..."));
        searchBox.setHasBorder(true);
        searchBox.setMaxLength(256);
        searchBox.setChangedListener(this::onSearchBoxChange);

        ButtonWidget findButton = new ButtonWidget((client.getWindow().getScaledWidth() / 2) + 60, 30, 50, 20, new LiteralText(findType.name()), button -> {
            findType = cycleResult(findType);
            button.setMessage(new LiteralText(findType.name()));
        });

        addButton(tabButton);
        addButton(searchBox);
        addButton(findButton);
    }

    public Filter.FindType cycleResult(Filter.FindType result) {
        if (result == null) {
            result = Filter.FindType.LITERAL;
        }
        Filter.FindType[] set = Filter.FindType.values();
        int current = 0;
        for (int i = 0; i < set.length; i++) {
            Filter.FindType res = set[i];
            if (res == result) {
                current = i;
            }
        }
        if (current >= set.length-1) {
            current = -1;
        }
        return set[current+1];
    }

    private void onSearchBoxChange(String s) {
        searchText = s;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 256) {
            client.openScreen(null);
            return true;
        }
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        AdvancedChat.getChatLogData().checkLast();
        renderBackground(matrices);
        drawCenteredString(matrices, client.textRenderer, "ChatLog", client.getWindow().getScaledWidth()/2, 20, ColorUtil.WHITE.color());
        int windowHeight = client.getWindow().getScaledHeight();
        int maxheight =  windowHeight - 90;
        List<ChatLogLine> filteredLines = AdvancedChat.getChatLogData().getFormattedMessages();
        int lines = 0;
        int lineHeight = AdvancedChat.configStorage.chatConfig.lineSpace;
        int bottomScreenOffset = 20;
        ColorUtil.SimpleColor textColor = AdvancedChat.configStorage.chatConfig.emptyText;
        if (tab != null) {
            filteredLines = filteredLines.stream().filter(filter -> Arrays.asList(filter.getTab()).contains(tab)).collect(Collectors.toList());
        }
        if (!searchText.equals("")) {
            try {
                filteredLines = filteredLines.stream().filter(filter -> SearchText.isMatch(filter.getText().getString(), searchText, findType)).collect(Collectors.toList());
            } catch (PatternSyntaxException e) {
                drawStringWithShadow(matrices, client.textRenderer, "Bad search!", 20, windowHeight - bottomScreenOffset - lineHeight, textColor.color());
                super.render(matrices, mouseX, mouseY, delta);
                return;
            }
        }


        if (filteredLines != null && filteredLines.size() > 0) {
            if (scrolledLines < 0) {
                scrolledLines = 0;
            }
            if (scrolledLines >= filteredLines.size()) {
                scrolledLines = filteredLines.size() - 1;
            }
            int startLine = scrolledLines + 1;
            int endLine = filteredLines.size();
            for (int i = 0; i + scrolledLines < filteredLines.size(); i++) {
                ChatLogLine line = filteredLines.get(i + scrolledLines);
                lines++;
                int relativeHeight = (lines * lineHeight);
                int height = (windowHeight - bottomScreenOffset) - relativeHeight;

                if (relativeHeight > maxheight) {
                    endLine = i + scrolledLines;
                    break;
                }
                drawTextWithShadow(matrices, client.textRenderer, line.getText(), 20, height + 1, textColor.color());
            }
            drawCenteredString(matrices, client.textRenderer, startLine + "-" + endLine + "/" + filteredLines.size(), client.getWindow().getScaledWidth() / 2, 10, ColorUtil.WHITE.color());

        } else {
            drawStringWithShadow(matrices, client.textRenderer, "Nothing found...", 20, windowHeight - bottomScreenOffset - lineHeight, textColor.color());
        }



        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        scrolledLines = scrolledLines + (int) Math.ceil(amount * 7);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


}
