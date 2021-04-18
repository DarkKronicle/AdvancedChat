package io.github.darkkronicle.advancedchat.chat.suggestors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.StringRange;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.chat.AdvancedSuggestion;
import io.github.darkkronicle.advancedchat.chat.AdvancedSuggestions;
import io.github.darkkronicle.advancedchat.chat.suggestors.gui.ShortcutEntryListWidget;
import io.github.darkkronicle.advancedchat.chat.suggestors.gui.ShortcutListWidget;
import io.github.darkkronicle.advancedchat.config.Filter;
import io.github.darkkronicle.advancedchat.config.gui.GuiFilterManager;
import io.github.darkkronicle.advancedchat.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchat.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchat.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.RawText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ShortcutSuggestor implements IMessageSuggestor, IJsonApplier, IScreenSupplier {


    @EqualsAndHashCode
    @Data
    public static class Shortcut {
        private String name;
        private String replace;

        public Shortcut(String name, String replace) {
            this.name = name.toLowerCase();
            this.replace = replace;
        }

        public static Shortcut fromJsonElement(JsonElement element) {
            if (!element.isJsonObject()) {
                return null;
            }
            JsonObject obj = element.getAsJsonObject();
            return new Shortcut(obj.get("name").getAsString(), obj.get("replace").getAsString());
        }

        public JsonObject toJsonElement() {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", name);
            obj.addProperty("replace", replace);
            return obj;
        }
    }

    public static Shortcut getRandomShortcut() {
        return new Shortcut(AdvancedChat.getRandomString(), AdvancedChat.getRandomString());
    }

    @Getter
    private List<Shortcut> shortcuts;

    public ShortcutSuggestor() {
        shortcuts = new ArrayList<>();

    }

    public void clearShortcuts() {
        shortcuts.clear();
    }

    public void addShortcut(Shortcut shortcut) {
        shortcuts.add(shortcut);
    }

    public boolean removeShortcut(Shortcut entry) {
        return this.shortcuts.remove(entry);
    }

    @Override
    public Optional<List<AdvancedSuggestions>> suggest(String string) {
        if (!string.contains(":")) {
            return Optional.empty();
        }
        ArrayList<AdvancedSuggestions> suggest = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ':') {
                continue;
            }
            int start = i;
            int end;
            while (true) {
                i++;
                if (i >= string.length()) {
                    end = i;
                    break;
                }
                char n = string.charAt(i);
                if (n == ' ') {
                    end = i;
                    break;
                } else if (n == ':') {
                    i--;
                    end = i + 1;
                    break;
                }
            }
            if (end - start < 1) {
                break;
            }
            StringRange range = new StringRange(start, end);
            suggest.add(new AdvancedSuggestions(range, getSuggestions(string.substring(start + 1, end), range)));
        }
        return Optional.of(suggest);
    }

    private List<AdvancedSuggestion> getSuggestions(String current, StringRange range) {
        ArrayList<AdvancedSuggestion> suggestions = new ArrayList<>();
        for (Shortcut shortcut : shortcuts) {
            if (current.length() == 0 || shortcut.name.toLowerCase().startsWith(current.toLowerCase())) {
                FluidText text = new FluidText();
                text.append(new RawText(shortcut.name, Style.EMPTY));
                suggestions.add(new AdvancedSuggestion(range, shortcut.replace, text, new RawText(shortcut.replace, Style.EMPTY)));
            }
        }
        return suggestions;
    }

    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        JsonArray shortcuts = new JsonArray();
        for (Shortcut shortcut : this.shortcuts) {
            shortcuts.add(shortcut.toJsonElement());
        }
        obj.add("shortcuts", shortcuts);
        return obj;
    }

    public void loadDefaultShortcuts() {
        shortcuts.add(new Shortcut("happy", "o((*^▽^*))o"));
        shortcuts.add(new Shortcut("owo","ÒwÓ"));
        shortcuts.add(new Shortcut("tablepain", "\u200E(ﾉಥ益ಥ）ﾉ ┻━┻"));
        shortcuts.add(new Shortcut("reee", "(ノಠ益ಠ)ノ彡┻━┻"));
        shortcuts.add(new Shortcut("worry", "(❁°͈▵°͈)"));
        shortcuts.add(new Shortcut("yeah", "⤴︎ ε=ε=(ง ˃̶͈̀ᗨ˂̶͈́)۶ ⤴︎"));
        shortcuts.add(new Shortcut("kiss", "(人´ω｀*)♡"));
        shortcuts.add(new Shortcut("yas", "╭( ･ㅂ･)و ̑̑"));
        shortcuts.add(new Shortcut("ouch", "~(>_<~)"));
        shortcuts.add(new Shortcut("sick", "(-﹏-。)"));
        shortcuts.add(new Shortcut("spook", "( ⚆ _ ⚆ )"));
        shortcuts.add(new Shortcut("yay", "＼(^o^)／"));
        shortcuts.add(new Shortcut("layingdown", "＿ﾉ乙(､ﾝ､)_"));
        shortcuts.add(new Shortcut("sleep", "(-_-) zzz"));
        shortcuts.add(new Shortcut("funny", "(ノ＞▽＜。)ノ"));
        shortcuts.add(new Shortcut("comedy", "(˵¯̴͒ꇴ¯̴͒˵)"));
        shortcuts.add(new Shortcut("yessir", "(-ω-ゞ"));
        shortcuts.add(new Shortcut("bruh", "(;¬_¬)"));
        shortcuts.add(new Shortcut("wink", "(｡•̀ᴗ-)"));
        shortcuts.add(new Shortcut("cri", "(;*△*;)"));
        shortcuts.add(new Shortcut("deadinside", "( ･ᴗ･̥̥̥ )"));
        shortcuts.add(new Shortcut("bow", "ヘ(_ _ヘ)"));
        shortcuts.add(new Shortcut("success", "(•̀ᴗ•́)و ̑̑"));
        shortcuts.add(new Shortcut("dead", "(×_×;)"));
        shortcuts.add(new Shortcut("shades", "(⌐■_■)"));
    }

    @Override
    public void load(JsonElement element) {
        shortcuts = new ArrayList<>();
        if (element == null || !element.isJsonObject()) {
            loadDefaultShortcuts();
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        JsonElement shorts = obj.get("shortcuts");
        if (shorts != null && shorts.isJsonArray()) {
            for (JsonElement el : shorts.getAsJsonArray()) {
                Shortcut shortcut = Shortcut.fromJsonElement(el);
                if (shortcut != null) {
                    shortcuts.add(shortcut);
                }
            }
        } else {
            loadDefaultShortcuts();
            return;
        }
        if (shortcuts.size() == 0) {
            loadDefaultShortcuts();
        }
    }

    @Override
    public Supplier<Screen> getScreen(@Nullable Screen parent) {
        return () -> new ShortcutScreen(parent);
    }

    public class ShortcutScreen extends GuiListBase<Shortcut, ShortcutEntryListWidget, ShortcutListWidget> {


        @Override
        public void initGui() {
            super.initGui();
            this.setListPosition(this.getListX(), 68);
            int y = 26;
            int x = this.width - 10;
            this.addButton(x, y, ButtonListener.Type.ADD_SHORTCUT, true);
            this.addButton(2, y, ButtonListener.Type.BACK, false);
        }

        protected ShortcutScreen(Screen parent) {
            super(10, 60);
            this.title = StringUtils.translate("advancedchat.screen.shortcut");
            this.setParent(parent);
        }

        protected int addButton(int x, int y, ButtonListener.Type type, boolean rightAlign) {
            ButtonGeneric button = new ButtonGeneric(x, y, -1, rightAlign, type.getDisplayName());
            this.addButton(button, new ButtonListener(type, this));

            return button.getWidth();
        }

        @Override
        protected ShortcutListWidget createListWidget(int listX, int listY) {
            return new ShortcutListWidget(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), null, ShortcutSuggestor.this, this);
        }

        @Override
        protected int getBrowserWidth() {
            return this.width - 20;
        }

        @Override
        protected int getBrowserHeight() {
            return this.height - 6 - this.getListY();
        }

        public void addShortcut() {
            Shortcut cut = null;
            for (int i = 0; i < 50; i++) {
                cut = getRandomShortcut();
                if (!ShortcutSuggestor.this.shortcuts.contains(cut)) {
                    break;
                }
            }
            shortcuts.add(cut);
            getListWidget().refreshEntries();
        }

        public void back() {
            this.closeGui(true);
        }

    }

    private static class ButtonListener implements IButtonActionListener {

        private final ButtonListener.Type type;
        private final ShortcutScreen gui;

        public ButtonListener(ButtonListener.Type type, ShortcutScreen gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.ADD_SHORTCUT) {
                this.gui.addShortcut();
            } else if (this.type == ButtonListener.Type.BACK) {
                this.gui.back();
            }
        }

        public enum Type {
            ADD_SHORTCUT("addshortcut"),
            BACK("back")
            ;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translate(translationKey);
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }
}
