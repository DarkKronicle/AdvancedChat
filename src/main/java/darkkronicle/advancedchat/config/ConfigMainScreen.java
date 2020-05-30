package darkkronicle.advancedchat.config;

import darkkronicle.advancedchat.AdvancedChatClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import net.minecraft.text.TranslatableText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigMainScreen extends Screen {

    private List<ButtonWidget> filters = new ArrayList<>();

    protected ConfigMainScreen() {
        super(new TranslatableText("advancedchat.config.main"));
    }

    public void init() {
        int i = 1;
        for (ConfigFilter filter : AdvancedChatClient.configObject.configFilters) {
            filters.add(buttonFromFilter(filter, i));
            i++;
        }
        for (ButtonWidget button : filters) {
            addButton(button);
        }
        addButton(new ButtonWidget(10, i*30+10, 60, 20, "Add filter", button -> {
            AdvancedChatClient.configObject.configFilters.add(new ConfigFilter());
            try {
                AdvancedChatClient.configManager.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AdvancedChatClient.mainFilter.reloadFilters();
            minecraft.openScreen(new ConfigMainScreen());
        }));
    }

    public ButtonWidget buttonFromFilter(ConfigFilter filter, int num) {
        ButtonWidget button = new ButtonWidget(10, num*30+10, 100, 20, filter.getName(), button1 -> {
            minecraft.openScreen(new ConfigFilterScreen(filter));
        });
        return button;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.minecraft.world == null) {
            this.renderDirtBackground(0);
        }
        renderBackground();
        drawCenteredString(this.font, getTitle().asFormattedString(), this.width / 2, (this.height - (this.height + 4 - 48)) / 2 - 4, 16777215);
        super.render(mouseX, mouseY, delta);
    }

}
