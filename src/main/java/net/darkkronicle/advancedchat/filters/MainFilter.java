package net.darkkronicle.advancedchat.filters;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MainFilter extends AbstractFilter {

    ArrayList<AbstractFilter> filters = new ArrayList<>();

    public MainFilter() {
        loadFilters();
    }

    @Override
    public Optional<StringRenderable> filter(StringRenderable text) {
        for (AbstractFilter filter : filters) {
            Optional<StringRenderable> newtext = filter.filter(text);
            if (newtext.isPresent()) {
                text = newtext.get();
            }
        }

        return Optional.empty();
    }

    public void loadFilters() {
       filters = new ArrayList<>();
        for (Filter filter : AdvancedChat.configStorage.filters) {
            if (filter.getReplaceType() != Filter.ReplaceType.NONE) {
                filters.add(new ReplaceFilter(filter.getFindString(), filter.getReplaceTo(), filter.getFindType(), filter.getReplaceType()));
            }
        }
    }
}
