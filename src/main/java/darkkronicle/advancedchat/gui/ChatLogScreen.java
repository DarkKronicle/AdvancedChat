package darkkronicle.advancedchat.gui;

import darkkronicle.advancedchat.AdvancedChatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatLogScreen extends Screen {
    private TextFieldWidget searchBox;
    private String searchText;
    private ListWidget entryList;

    private CheckboxWidget checkbox;

    public ChatLogScreen() {
        super(new TranslatableText("advancedchat.screen.chatlog"));
    }

    @Override
    protected void init() {
        this.buttons.clear();
        searchBox = new TextFieldWidget(this.font, (MinecraftClient.getInstance().getWindow().getScaledWidth()/2)-50, 30, 100, 20, "Search...");
        searchBox.setHasBorder(true);
        searchBox.setMaxLength(256);
        searchBox.setChangedListener(this::onSearchBoxChange);
        checkbox = new CheckboxWidget((MinecraftClient.getInstance().getWindow().getScaledWidth()/2)+55, 30, 20, 20, "Ignore Case", false);

        searchText = "";
        addButton(searchBox);
        addButton(checkbox);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.minecraft.world == null) {
            this.renderDirtBackground(0);
        }
        renderBackground();
        drawCenteredString(this.font, getTitle().asFormattedString(), this.width / 2, (this.height - (this.height + 4 - 48)) / 2 - 4, 16777215);
        List<ChatHudLine> fullMessages = AdvancedChatClient.getChatHud().getMessages();
        List<String> messages = new ArrayList<>();
        if (fullMessages != null && fullMessages.size() != 0) {
            for (ChatHudLine message : fullMessages) {
                messages.add(message.getText().asFormattedString());
            }
            if (searchText.length() != 0) {
                if (checkbox.isChecked()) {
                    messages = messages.stream().filter(string -> string.toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
                } else {
                    messages = messages.stream().filter(string -> string.contains(searchText)).collect(Collectors.toList());
                }
            }
            int i = 0;
            for (String line : messages) {
                i++;
                drawString(this.font, line, 10, i*9+50, 16777215);
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
}
