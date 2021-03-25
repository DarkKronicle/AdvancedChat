package net.darkkronicle.advancedchat.config.gui.widgets;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import lombok.Getter;
import lombok.Setter;
import net.darkkronicle.advancedchat.config.Filter;
import net.darkkronicle.advancedchat.util.SearchUtils;
import net.minecraft.client.font.TextRenderer;

import java.util.List;
import java.util.Optional;

public class WidgetIntBox extends GuiTextFieldGeneric {

    @Setter
    @Getter
    private Runnable apply = null;

    public WidgetIntBox(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, textRenderer);
        this.setTextPredicate((text) -> {
            if (text.equals("")) {
                return true;
            }
            try {
                // Only allow numbers!
                Integer.valueOf(text);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        });
        this.setDrawsBackground(true);
    }

    public Integer getInt() {
        String text = this.getText();
        if (text == null || text.length() == 0) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Extra catch
            Optional<List<SearchUtils.StringMatch>> omatches = SearchUtils.findMatches(text, "[0-9]+", Filter.FindType.REGEX);
            if (!omatches.isPresent()) {
                return null;
            }
            for (SearchUtils.StringMatch m : omatches.get()) {
                try {
                    return Integer.parseInt(m.match);
                } catch (NumberFormatException err) {
                    return null;
                }
            }
        }
        return null;
    }

}
