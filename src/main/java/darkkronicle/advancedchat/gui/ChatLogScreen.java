/* AdvancedChat: A Minecraft Mod to modify the chat.
Copyright (C) 2020 DarkKronicle

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.*/

package darkkronicle.advancedchat.gui;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ClothConfigScreen;
import darkkronicle.advancedchat.config.ConfigMainScreen;
import darkkronicle.advancedchat.filters.FilteredMessage;
import darkkronicle.advancedchat.util.FormattedText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ChatLogScreen extends Screen {
    private TextFieldWidget searchBox;
    private TextFieldWidget chatField;
    private String searchText;
    private CheckboxWidget checkbox;
    private CheckboxWidget regex;
    private CommandSuggestor commandSuggestor;
    private int scrolledLine = 1;
    private FilteredMessage.FilterResult filter = FilteredMessage.FilterResult.UNKNOWN;

    public ChatLogScreen() {
        super(new TranslatableText("advancedchat.screen.chatlog"));
    }

    private AdvancedChatScreen chatScreen;

    @Override
    protected void init() {
        MinecraftClient client = MinecraftClient.getInstance();

        chatScreen = new AdvancedChatScreen("");
        this.buttons.clear();
        searchBox = new TextFieldWidget(this.font, (client.getWindow().getScaledWidth() / 2) - 50, 30, 100, 20, "Search...");
        searchBox.setHasBorder(true);
        searchBox.setMaxLength(256);
        searchBox.setChangedListener(this::onSearchBoxChange);

        regex = new CheckboxWidget((client.getWindow().getScaledWidth() / 2) + 55, 50, 20, 20, "Regex", false);
        checkbox = new CheckboxWidget((client.getWindow().getScaledWidth() / 2) + 55, 30, 20, 20, "Ignore Case", false);

        searchText = "";

        this.chatField = new TextFieldWidget(this.font, 4, client.getWindow().getScaledHeight() - 16, client.getWindow().getScaledWidth() - 8, 12, "Chatfield");
        this.chatField.setMaxLength(256);
        this.chatField.setText("");
        this.chatField.setHasBorder(true);
        this.chatField.setChangedListener(this::onChatFieldUpdate);

        this.commandSuggestor = new CommandSuggestor(this.minecraft, this, this.chatField, this.font, false, false, 1, 10, true, -805306368);
        this.commandSuggestor.refresh();

        addButton(searchBox);
        addButton(checkbox);
        addButton(regex);
        addButton(chatField);

        addButton(new ButtonWidget(10, 10, 50, 20, "Filters", button -> {
            minecraft.openScreen(new ConfigMainScreen());
        }));
        addButton(new ButtonWidget(70, 10, 70, 20, "Configuration", button -> {
            minecraft.openScreen(new ClothConfigScreen().getConfigScreen());
        }));
        addButton(new ButtonWidget(MinecraftClient.getInstance().getWindow().getScaledWidth() - 60, 10, 50, 20, "All", button -> {
            filter = cycleResult(filter);
            if (filter == FilteredMessage.FilterResult.UNKNOWN) {
                button.setMessage("ALL");
            } else {
                button.setMessage(filter.name());
            }
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.minecraft.world == null) {
            this.renderDirtBackground(0);
        }
        boolean upDown = AdvancedChatClient.configObject.linesUpDown;
        renderBackground();

        List<AdvancedChatHudLine> fullMessages = AdvancedChatClient.getChatHud().getMessages();
        List<String> messages = new ArrayList<>();
        String toreplace = AdvancedChatClient.configObject.replaceFormat.replaceAll("&", "§");

        if (fullMessages != null && fullMessages.size() != 0) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(AdvancedChatClient.configObject.timeFormat);
            fullMessages = fullMessages.stream().filter(line -> line.doesInclude(filter) || filter == FilteredMessage.FilterResult.UNKNOWN).collect(Collectors.toList());
            for (AdvancedChatHudLine message : fullMessages) {
                String text = message.getText().asFormattedString();
                if (message.getRepeats() > 1) {
                    text = text + "§7 (" + message.getRepeats() + ")";
                }
                if (AdvancedChatClient.configObject.showTime) {
                    text = toreplace.replaceAll("%TIME%", message.getTime().format(format)) + "§f" + text;
                }
                messages.add(text);
            }
            if (searchText.length() != 0) {
                if (!regex.isChecked()) {
                    if (checkbox.isChecked()) {
                        messages = messages.stream().filter(string -> string.toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
                    } else {
                        messages = messages.stream().filter(string -> string.contains(searchText)).collect(Collectors.toList());
                    }
                } else {
                    try {
                        Pattern p = Pattern.compile(searchText);
                        messages = messages.stream().filter(string -> {
                            Matcher matcher = p.matcher(string);
                            return matcher.find();
                        }).collect(Collectors.toList());
                    } catch (PatternSyntaxException e) {
                        drawString(this.font, "Bad regex!", 10, 59, 16777215);
                        messages.clear();
                    }

                }
            }
            if (messages.size() == 0) {
                drawString(this.font, "Nothing found :(", 10, 59, 16777215);
                super.render(mouseX, mouseY, delta);
                return;
            }

            List<String> linedMessages = new ArrayList<>();
            for (String message : messages) {
                List<Text> lined = Texts.wrapLines(FormattedText.formatText(message), width - 20, this.font, false, true);
                if (!upDown) {
                    Collections.reverse(lined);
                }
                for (Text text : lined) {
                    linedMessages.add(text.asFormattedString());
                }
            }

            int linesPerPage = (int) Math.ceil(((double) (minecraft.getWindow().getScaledHeight() / 9) - 10));
            if (scrolledLine < 1) {
                scrolledLine = 1;
            }
            if (scrolledLine > linedMessages.size()) {
                scrolledLine = linedMessages.size();
            }
            int startLine = scrolledLine;
            int endLine = scrolledLine + linesPerPage;
            if (endLine > linedMessages.size()) {
                endLine = linedMessages.size();
            }
            drawCenteredString(this.font, startLine + "-" + endLine + "/" + linedMessages.size(), minecraft.getWindow().getScaledWidth() / 2, 10, 16777215);
            int pageLine = 1;

            if (upDown) {
                for (int i = startLine; i <= endLine; i++) {
                    int currentPage = i - 1;
                    String line = linedMessages.get(currentPage);
                    drawString(this.font, line, 10, pageLine * 9 + 50, 16777215);
                    pageLine++;
                }
            } else {
                int height = minecraft.getWindow().getScaledHeight();
                for (int i = startLine; i <= endLine; i++) {
                    int currentPage = i - 1;
                    String line = linedMessages.get(currentPage);
                    drawString(this.font, line, 10, height - (pageLine * 9 + 20), 16777215);
                    pageLine++;
                }
            }
        } else {
            drawString(this.font, "No chat messages yet!", 10, 59, 16777215);
        }


        this.chatField.render(mouseX, mouseY, delta);
        this.commandSuggestor.render(mouseX, mouseY);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void onSearchBoxChange(String searchText) {
        this.searchText = searchText;
    }

    public FilteredMessage.FilterResult cycleResult(FilteredMessage.FilterResult result) {
        if (result == null || result == FilteredMessage.FilterResult.UNKNOWN) {
            result = FilteredMessage.FilterResult.UNKNOWN;
        }
        FilteredMessage.FilterResult[] set = FilteredMessage.FilterResult.values();
        int current = 0;
        for (int i = 0; i < set.length; i++) {
            FilteredMessage.FilterResult res = set[i];
            if (res == result) {
                current = i;
            }
        }
        if (current >= set.length-1) {
            current = -1;
        }
        return set[current+1];
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (AdvancedChatClient.configObject.linesUpDown) {
            scrolledLine = scrolledLine + (int) Math.ceil(amount * -7);
        } else {
            scrolledLine = scrolledLine + (int) Math.ceil(amount * 7);
        }
        return false;
    }

    public void onChatFieldUpdate(String chatText) {
        String string = this.chatField.getText();
        this.commandSuggestor.setWindowActive(!string.equals(""));
        this.commandSuggestor.refresh();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 256) {
            this.minecraft.openScreen((Screen) null);
            return true;
        } else if (keyCode != 257 && keyCode != 335) {
            if (keyCode == 265) {
                this.setChatFromHistory(-1);
                return true;
            } else if (keyCode == 264) {
                this.setChatFromHistory(1);
                return true;
            } else {
                return false;
            }
        } else {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
                chatField.setText("");
            }

            return true;
        }
    }

    public void setChatFromHistory(int i) {
        int j = chatScreen.getMessageHistorySize() + i;
        int k = this.minecraft.inGameHud.getChatHud().getMessageHistory().size();
        j = MathHelper.clamp(j, 0, k);
        if (j != chatScreen.getMessageHistorySize()) {
            if (j == k) {
                chatScreen.setMessageHistorySize(k);
                this.chatField.setText(chatScreen.getMessHist());
            } else {
                if (chatScreen.getMessageHistorySize() == k) {
                    chatScreen.setMessHist(this.chatField.getText());
                }

                this.chatField.setText((String) this.minecraft.inGameHud.getChatHud().getMessageHistory().get(j));
                this.commandSuggestor.setWindowActive(false);
                chatScreen.setMessageHistorySize(j);
            }
        }
    }
}
