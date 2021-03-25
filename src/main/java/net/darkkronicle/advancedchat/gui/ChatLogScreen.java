package net.darkkronicle.advancedchat.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.config.ConfigStorage;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.ColorUtil;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ChatLogScreen extends GuiBase {

    private MinecraftClient client;

    private AbstractChatTab tab = null;

    private int scrolledLines = 0;

    private TextFieldWidget searchBox;
    private String searchText;

    private CheckboxWidget searchFull;

    private Filter.FindType findType = Filter.FindType.LITERAL;

    public ChatLogScreen() {
        this.title = StringUtils.translate("advancedchat.screen.chatlog");
    }

    public static int getWidth() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledWidth() - 40;
    }

    @Override
    public void initGui() {
        super.initGui();
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
        searchBox.setDrawsBackground(true);
        searchBox.setMaxLength(256);
        searchBox.setChangedListener(this::onSearchBoxChange);

        searchFull = new CheckboxWidget((client.getWindow().getScaledWidth() / 2) + 120, 30, 20, 20, new LiteralText("Search Full Messages"), true);

        ButtonWidget findButton = new ButtonWidget((client.getWindow().getScaledWidth() / 2) + 60, 30, 50, 20, new LiteralText(findType.name()), button -> {
            findType = cycleResult(findType);
            button.setMessage(new LiteralText(findType.name()));
        });

        addButton(tabButton);
        addButton(searchBox);
        addButton(searchFull);
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
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, client.textRenderer, "ChatLog", client.getWindow().getScaledWidth()/2, 20, ColorUtil.WHITE.color());
        int windowHeight = client.getWindow().getScaledHeight();
        int maxheight =  windowHeight - 90;
        List<ChatLogLine> filteredLines = AdvancedChat.getChatLogData().getFormattedMessages();
        int lines = 0;
        int lineHeight = ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue();
        int bottomScreenOffset = 20;
        ColorUtil.SimpleColor textColor = ConfigStorage.ChatScreen.EMPTY_TEXT_COLOR.config.getSimpleColor();
        if (tab != null) {
            filteredLines = filteredLines.stream().filter(filter -> Arrays.asList(filter.getTab()).contains(tab)).collect(Collectors.toList());
        }
        if (!searchText.equals("")) {
            try {
                if (searchFull.isChecked()) {
                    ArrayList<UUID> uuids = new ArrayList<>();
                    filteredLines.forEach(line -> {
                        if (SearchUtils.isMatch(line.getText().getString(), searchText, findType)) {
                            uuids.add(line.getUuid());
                        }
                    });
                    filteredLines = filteredLines.stream().filter(filter -> uuids.contains(filter.getUuid())).collect(Collectors.toList());
                } else {
                    filteredLines = filteredLines.stream().filter(filter -> SearchUtils.isMatch(filter.getText().getString(), searchText, findType)).collect(Collectors.toList());
                }
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

        Style style = this.getText(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }

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

    public int getVisibleLineCount() {
        int maxHeight = client.getWindow().getScaledHeight() - 90;
        int lineHeight = ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue();
        int lineCount = (int) Math.floor((float) maxHeight / lineHeight);
        return lineCount;
    }

    public Style getText(double mouseX, double mouseY) {
        double trueX = mouseX - 2;
        double trueY = (double)this.client.getWindow().getScaledHeight() - mouseY - 20;
//      trueX = MathHelper.floor(trueX);
//      trueY = MathHelper.floor(trueY * (AdvancedChat.configStorage.chatConfig.lineSpace + 1.0D));
        if (trueX >= 0.0D && trueY >= 0.0D) {
            int numOfMessages = Math.min(this.getVisibleLineCount(), AdvancedChat.getChatLogData().getFormattedMessages().size());
            if (trueX <= (double) MathHelper.floor((double) getWidth())) {
                if (trueY < (double)(9 * numOfMessages + numOfMessages)) {
                    int lineNum = (int)(trueY / ConfigStorage.ChatScreen.LINE_SPACE.config.getIntegerValue() + (double)this.scrolledLines);
                    if (lineNum >= 0 && lineNum < AdvancedChat.getChatLogData().getFormattedMessages().size() && lineNum <= getVisibleLineCount() + scrolledLines) {
                        ChatLogLine chatHudLine = AdvancedChat.getChatLogData().getFormattedMessages().get(lineNum);
                        return this.client.textRenderer.getTextHandler().getStyleAt(chatHudLine.getText(), (int)trueX - 20);
                    }
                }
            }

        }
        return null;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            AdvancedChatHud chatHud = AdvancedChat.getAdvancedChatHud();

            Style style = chatHud.getText(mouseX, mouseY);
            if (style != null && this.handleTextClick(style)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);

    }

}
