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

package darkkronicle.advancedchat.config;

import darkkronicle.advancedchat.AdvancedChatClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigMainScreen extends Screen {
    /*
    Class to allow customization of different filters in a nice gui.
     */

    //TODO Make gui include scrolling.

    private List<ButtonWidget> filters = new ArrayList<>();

    public ConfigMainScreen() {
        super(new LiteralText("Filter Configuration"));
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
        addButton(new ButtonWidget(minecraft.getWindow().getScaledWidth()-70, 10, 60, 20, "Add filter", button -> {
            AdvancedChatClient.configObject.configFilters.add(new ConfigFilter());
            try {
                AdvancedChatClient.configManager.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            minecraft.openScreen(new ConfigMainScreen());
        }));
    }

    public ButtonWidget buttonFromFilter(ConfigFilter filter, int num) {
        int x = 10;
        int y = num*30+10;
        int height = minecraft.getWindow().getScaledHeight();
        int numPerRow = (int)Math.ceil((double)(height - 20) / 20);
        if (y > height-30) {
            int where = (num + 1) % numPerRow;
            y = where * 30 + 10;
            x = 110;
        }
        ButtonWidget button = new ButtonWidget(x, y, 100, 20, filter.getName(), button1 -> {
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
