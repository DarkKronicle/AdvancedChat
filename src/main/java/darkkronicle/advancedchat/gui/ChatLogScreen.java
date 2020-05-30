package darkkronicle.advancedchat.gui;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigMainScreen;
import darkkronicle.advancedchat.filters.FilteredMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatLogScreen extends Screen {
    private TextFieldWidget searchBox;
    private String searchText;
    private CheckboxWidget checkbox;
    private int scrolledLine = 1;
    private FilteredMessage.FilterResult filter = FilteredMessage.FilterResult.UNKNOWN;

    public ChatLogScreen() {
        super(new TranslatableText("advancedchat.screen.chatlog"));
    }

    @Override
    protected void init() {
        this.buttons.clear();
        searchBox = new TextFieldWidget(this.font, (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - 50, 30, 100, 20, "Search...");
        searchBox.setHasBorder(true);
        searchBox.setMaxLength(256);
        searchBox.setChangedListener(this::onSearchBoxChange);
        checkbox = new CheckboxWidget((MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) + 55, 30, 20, 20, "Ignore Case", false);

        searchText = "";
        addButton(searchBox);
        addButton(checkbox);

        addButton(new ButtonWidget(10, 10, 50, 20, "Filters", button -> {
            minecraft.openScreen(new ConfigMainScreen());
        }));
        addButton(new ButtonWidget(MinecraftClient.getInstance().getWindow().getScaledWidth()-60, 10, 50, 20, filter.name(), button -> {
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
        renderBackground();
        drawCenteredString(this.font, getTitle().asFormattedString(), this.width / 2, (this.height - (this.height + 4 - 48)) / 2 - 4, 16777215);
        List<AdvancedChatHudLine> fullMessages = AdvancedChatClient.getChatHud().getMessages();
        List<String> messages = new ArrayList<>();
        if (fullMessages != null && fullMessages.size() != 0) {
            fullMessages = fullMessages.stream().filter(line -> line.getType() == filter || filter == FilteredMessage.FilterResult.UNKNOWN).collect(Collectors.toList());
            for (AdvancedChatHudLine message : fullMessages) {
                messages.add(message.getText().asFormattedString());
            }
            if (searchText.length() != 0) {
                if (checkbox.isChecked()) {
                    messages = messages.stream().filter(string -> string.toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
                } else {
                    messages = messages.stream().filter(string -> string.contains(searchText)).collect(Collectors.toList());
                }
            }
            if (messages.size() == 0) {
                drawString(this.font, "Nothing found :(", 10, 59, 16777215);
                super.render(mouseX, mouseY, delta);
                return;
            }

            int linesPerPage = (int) Math.ceil(((double) (minecraft.getWindow().getScaledHeight() / 9) - 10));
            if (scrolledLine < 1) {
                scrolledLine = 1;
            }
            if (scrolledLine > messages.size()) {
                scrolledLine = messages.size();
            }
            int startLine = scrolledLine;
            int endLine = scrolledLine + linesPerPage;
            if (endLine > messages.size()) {
                endLine = messages.size();
            }
            drawCenteredString(this.font, startLine + "-" + endLine, minecraft.getWindow().getScaledWidth() / 2, minecraft.getWindow().getScaledHeight() - 10, 16777215);
            int pageLine = 1;
            for (int i = startLine; i <= endLine; i++) {
                int currentPage = i - 1;
                String line = messages.get(currentPage);
                drawString(this.font, line, 10, pageLine * 9 + 50, 16777215);
                pageLine++;
            }
        } else {
            drawString(this.font, "No chat messages yet!", 10, 59, 16777215);
        }

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
        scrolledLine = scrolledLine + (int)Math.ceil(amount*7);
        return false;
    }
}
