package io.github.darkkronicle.advancedchat.chat;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import io.github.darkkronicle.advancedchat.util.RawText;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;

/**
 * Suggestion that contains render text, suggested text, suggested start/stop, and tooltip.
 */
@Environment(EnvType.CLIENT)
public class AdvancedSuggestion extends Suggestion {

    @Nonnull
    @Getter
    private final Text render;

    /**
     * @param range Range from the original string where it is recommending
     * @param text Suggested text to use
     * @param render How the suggestion will render
     * @param tooltip Message to show up on hover
     */
    public AdvancedSuggestion(StringRange range, String text, Text render, Message tooltip) {
        super(range, text, tooltip);
        if (render == null) {
            this.render = new RawText(text, Style.EMPTY);
        } else {
            this.render = render;
        }
    }

    public AdvancedSuggestion(StringRange range, String text) {
        this(range, text, null, null);
    }

    @Override
    public int compareTo(final Suggestion o) {
        if (o instanceof AdvancedSuggestion) {
            return render.getString().compareTo(((AdvancedSuggestion) o).getRender().getString());
        }
        return render.getString().compareTo(o.getText());
    }

    @Override
    public int compareToIgnoreCase(final Suggestion o) {
        if (o instanceof AdvancedSuggestion) {
            return render.getString().compareToIgnoreCase(((AdvancedSuggestion) o).getRender().getString());
        }
        return render.getString().compareToIgnoreCase(o.getText());
    }

    /**
     * Create's an {@link AdvancedSuggestion} from an {@link Suggestion}
     * @param suggestion Suggestion to convert
     * @return New objeect
     */
    public static AdvancedSuggestion fromSuggestion(Suggestion suggestion) {
        return new AdvancedSuggestion(suggestion.getRange(), suggestion.getText(), null, suggestion.getTooltip());
    }
}
