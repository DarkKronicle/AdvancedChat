package io.github.darkkronicle.advancedchat.gui;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fi.dy.masa.malilib.util.KeyCodes;
import io.github.darkkronicle.advancedchat.chat.AdvancedSuggestion;
import io.github.darkkronicle.advancedchat.chat.ChatSuggestor;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.chat.ChatFormatter;
import io.github.darkkronicle.advancedchat.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ChatSuggestorGui {
    private final MinecraftClient client;
    private final Screen owner;
    private final TextFieldWidget textField;
    private final TextRenderer textRenderer;
    private final boolean slashOptional;
    private final boolean suggestingWhenEmpty;
    private final int inWindowIndexOffset;
    private final int maxSuggestionSize;
    private final boolean chatScreenSized;
    private final List<OrderedText> messages = Lists.newArrayList();
    private int x;
    private int width;
    private SuggestionWindow window;

    private boolean windowActive;
    private boolean completingSuggestions;

    private final ChatFormatter formatter;
    private final ChatSuggestor suggestor;

    public ChatSuggestorGui(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, boolean slashRequired, boolean suggestingWhenEmpty, int inWindowIndexOffset, int maxSuggestionSize, boolean chatScreenSized) {
        this.client = client;
        this.owner = owner;
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.slashOptional = slashRequired;
        this.suggestingWhenEmpty = suggestingWhenEmpty;
        this.inWindowIndexOffset = inWindowIndexOffset;
        this.maxSuggestionSize = maxSuggestionSize;
        this.chatScreenSized = chatScreenSized;
        this.suggestor = new ChatSuggestor(textField);
        this.formatter = new ChatFormatter(textField, suggestor);
        this.textField.setRenderTextProvider(this::provideRenderText);
    }

    public void setWindowActive(boolean windowActive) {
        this.windowActive = windowActive;
        if (!windowActive) {
            this.window = null;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.window != null && this.window.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.owner.getFocused() == this.textField && keyCode == KeyCodes.KEY_TAB) {
            this.showSuggestions(true);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double amount) {
        return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0D, 1.0D));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.window != null && this.window.mouseClicked((int)mouseX, (int)mouseY, button);
    }

    public void showSuggestions(boolean narrateFirstSuggestion) {
        if (!suggestor.isDone()) {
            return;
        }
        List<AdvancedSuggestion> suggestions = suggestor.getSuggestions();
        if (suggestions.isEmpty()) {
            return;
        }
        int width = 0;
        for (AdvancedSuggestion value : suggestions) {
            // Have suggestion be as wide as the biggest
            width = Math.max(width, this.textRenderer.getWidth(value.getRender()));
        }

        int x = MathHelper.clamp(this.textField.getCharacterX(suggestor.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - width);
        int y = this.chatScreenSized ? this.owner.height - 12 : 72;
        this.window = new SuggestionWindow(x, y, width, suggestions, narrateFirstSuggestion);
    }



    public void refresh() {
        String currentText = this.textField.getText();
        if (this.suggestor.getParse() != null && !this.suggestor.getParse().getReader().getString().equals(currentText)) {
            // Set it to null to signify that if it is still a command, to parse it
            this.suggestor.invalidateParse();
        }

        if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
        }

        this.messages.clear();
        StringReader stringReader = new StringReader(currentText);
        boolean command = stringReader.canRead() && stringReader.peek() == '/';
        if (command) {
            stringReader.skip();
        }

        boolean suggestCommand = this.slashOptional || command;
        if (suggestCommand) {
            int cursorIndex = this.textField.getCursor();
            this.suggestor.updateParse(stringReader);
            // Index 1 will enforce that the player typed at LEAST ONE character
            if ((this.suggestingWhenEmpty || cursorIndex >= stringReader.getCursor()) && (this.window == null || !this.completingSuggestions)) {
                this.suggestor.updateCommandSuggestions(() -> {
                    if (this.suggestor.isDone()) {
                        this.showIfActive();
                    }
                });
            }
        } else {
            this.suggestor.updateChatSuggestions();
        }

    }


    private static OrderedText formatException(CommandSyntaxException exception) {
        Text text = Texts.toText(exception.getRawMessage());
        String string = exception.getContext();
        return string == null ? text.asOrderedText() : (new TranslatableText("command.context.parse_error", text, exception.getCursor(), string)).asOrderedText();
    }

    private void showIfActive() {
        if (this.textField.getCursor() == this.textField.getText().length()) {
            if (this.suggestor.getSuggestions().isEmpty() && !this.suggestor.getParse().getExceptions().isEmpty()) {
                int builtInExceptions = 0;

                for (Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> commandNodeCommandSyntaxExceptionEntry : this.suggestor.getParse().getExceptions().entrySet()) {
                    CommandSyntaxException commandSyntaxException = commandNodeCommandSyntaxExceptionEntry.getValue();
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++builtInExceptions;
                    } else {
                        this.messages.add(formatException(commandSyntaxException));
                    }
                }

                if (builtInExceptions > 0) {
                    this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else if (this.suggestor.getParse().getReader().canRead()) {
                this.messages.add(formatException(CommandManager.getException(this.suggestor.getParse())));
            }
        }
        this.x = 0;
        this.width = this.owner.width;
        if (this.messages.isEmpty()) {
            this.showUsages(Formatting.GRAY);
        }
        this.window = null;
        if (this.windowActive) {
            this.showSuggestions(false);
        }

    }

    private void showUsages(Formatting formatting) {
        CommandContextBuilder<CommandSource> commandContextBuilder = this.suggestor.getParse().getContext();
        SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
        Map<CommandNode<CommandSource>, String> map = this.client.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
        List<OrderedText> list = new ArrayList<>();
        int i = 0;
        Style style = Style.EMPTY.withColor(formatting);

        for (Map.Entry<CommandNode<CommandSource>, String> commandNodeStringEntry : map.entrySet()) {
            if (!(commandNodeStringEntry.getKey() instanceof LiteralCommandNode)) {
                list.add(OrderedText.styledForwardsVisitedString(commandNodeStringEntry.getValue(), style));
                i = Math.max(i, this.textRenderer.getWidth(commandNodeStringEntry.getValue()));
            }
        }

        if (!list.isEmpty()) {
            this.messages.addAll(list);
            this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
            this.width = i;
        }

    }

    private OrderedText provideRenderText(String original, int firstCharacterIndex) {
        return this.formatter.apply(original, firstCharacterIndex);
    }

    @Nullable
    private static String getSuggestionSuffix(String original, String suggestion) {
        return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY) {
        if (this.window != null) {
            this.window.render(matrices, mouseX, mouseY);
        } else {
            int i = 0;

            for (OrderedText message : this.messages) {
                i++;
                int j = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * i : 72 + 12 * i;
                DrawableHelper.fill(matrices, this.x - 1, j, this.x + this.width + 1, j + 12, ConfigStorage.ChatSuggestor.BACKGROUND_COLOR.config.getSimpleColor().color());
                if (message != null) {
                    this.textRenderer.drawWithShadow(matrices, message, (float) this.x, (float) (j + 2), -1);
                }
            }
        }
    }

    public String getNarration() {
        return this.window != null ? "\n" + this.window.getNarration() : "";
    }

    @Environment(EnvType.CLIENT)
    public class SuggestionWindow {
        private final Rect2i area;
        private final String typedText;
        private final List<AdvancedSuggestion> suggestions;
        private int inWindowIndex;
        private int selection;
        private Vec2f mouse;
        private boolean completed;
        private int lastNarrationIndex;

        private SuggestionWindow(int x, int y, int width, List<AdvancedSuggestion> list, boolean narrateFirstSuggestion) {
            this.mouse = Vec2f.ZERO;
            int renderX = x - 1;
            int renderY = ChatSuggestorGui.this.chatScreenSized ? y - 3 - Math.min(list.size(), ChatSuggestorGui.this.maxSuggestionSize) * 12 : y;
            this.area = new Rect2i(renderX, renderY, width + 1, Math.min(list.size(), ChatSuggestorGui.this.maxSuggestionSize) * 12);
            this.typedText = ChatSuggestorGui.this.textField.getText();
            this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
            this.suggestions = list;
            this.select(0);
        }

        public void render(MatrixStack matrices, int mouseX, int mouseY) {
            int suggestionSize = Math.min(this.suggestions.size(), ChatSuggestorGui.this.maxSuggestionSize);
            boolean moreBelow = this.inWindowIndex > 0;
            boolean moreAbove = this.suggestions.size() > this.inWindowIndex + suggestionSize;
            boolean more = moreBelow || moreAbove;
            boolean updateMouse = this.mouse.x != (float) mouseX || this.mouse.y != (float) mouseY;
            if (updateMouse) {
                this.mouse = new Vec2f((float) mouseX, (float) mouseY);
            }

            if (more) {
                // Draw lines to signify that there is more
                DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), ConfigStorage.ChatSuggestor.BACKGROUND_COLOR.config.getSimpleColor().color());
                DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, ConfigStorage.ChatSuggestor.BACKGROUND_COLOR.config.getSimpleColor().color());
                int x;
                if (moreBelow) {
                    // Dotted
                    for (x = 0; x < this.area.getWidth(); ++x) {
                        if (x % 2 == 0) {
                            DrawableHelper.fill(matrices, this.area.getX() + x, this.area.getY() - 1, this.area.getX() + x + 1, this.area.getY(), ColorUtil.WHITE.color());
                        }
                    }
                }

                if (moreAbove) {
                    // Dotted
                    for (x = 0; x < this.area.getWidth(); ++x) {
                        if (x % 2 == 0) {
                            DrawableHelper.fill(matrices, this.area.getX() + x, this.area.getY() + this.area.getHeight(), this.area.getX() + x + 1, this.area.getY() + this.area.getHeight() + 1, ColorUtil.WHITE.color());
                        }
                    }
                }
            }

            boolean hover = false;

            for (int s = 0; s < suggestionSize; ++s) {
                AdvancedSuggestion suggestion = this.suggestions.get(s + this.inWindowIndex);
                DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() + 12 * s, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * s + 12, ConfigStorage.ChatSuggestor.BACKGROUND_COLOR.config.getSimpleColor().color());
                if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY > this.area.getY() + 12 * s && mouseY < this.area.getY() + 12 * s + 12) {
                    if (updateMouse) {
                        this.select(s + this.inWindowIndex);
                    }

                    hover = true;
                }

                ChatSuggestorGui.this.textRenderer.drawWithShadow(matrices, suggestion.getRender(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * s), (s + this.inWindowIndex) == this.selection ?
                        ConfigStorage.ChatSuggestor.HIGHLIGHT_COLOR.config.getSimpleColor().color() :
                        ConfigStorage.ChatSuggestor.UNHIGHLIGHT_COLOR.config.getSimpleColor().color());
            }

            if (hover) {
                Message message = this.suggestions.get(this.selection).getTooltip();
                if (message != null) {
                    ChatSuggestorGui.this.owner.renderTooltip(matrices, Texts.toText(message), mouseX, mouseY);
                }
            }

        }

        public boolean mouseClicked(int x, int y, int button) {
            if (!this.area.contains(x, y)) {
                return false;
            }
            int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
            if (i >= 0 && i < this.suggestions.size()) {
                this.select(i);
                this.complete();
            }

            return true;
        }

        public boolean mouseScrolled(double amount) {
            int x = (int)(ChatSuggestorGui.this.client.mouse.getX() * (double) ChatSuggestorGui.this.client.getWindow().getScaledWidth() / (double) ChatSuggestorGui.this.client.getWindow().getWidth());
            int y = (int)(ChatSuggestorGui.this.client.mouse.getY() * (double) ChatSuggestorGui.this.client.getWindow().getScaledHeight() / (double) ChatSuggestorGui.this.client.getWindow().getHeight());
            if (this.area.contains(x, y)) {
                this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.size() - ChatSuggestorGui.this.maxSuggestionSize, 0));
                return true;
            }
            return false;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == KeyCodes.KEY_UP) {
                this.scroll(-1);
                this.completed = false;
                return true;
            }
            if (keyCode == KeyCodes.KEY_DOWN) {
                this.scroll(1);
                this.completed = false;
                return true;
            }
            if (keyCode == KeyCodes.KEY_TAB) {
                if (this.completed) {
                    this.scroll(Screen.hasShiftDown() ? -1 : 1);
                }

                this.complete();
                return true;
            }
            if (keyCode == KeyCodes.KEY_ESCAPE) {
                this.discard();
                return true;
            }
            return false;
        }

        public void scroll(int offset) {
            this.select(this.selection + offset);
            int windowIndex = this.inWindowIndex;
            int maxInWindow = this.inWindowIndex + ChatSuggestorGui.this.maxSuggestionSize - 1;
            // Moving window logic
            if (this.selection < windowIndex) {
                this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - ChatSuggestorGui.this.maxSuggestionSize, 0));
            } else if (this.selection > maxInWindow) {
                this.inWindowIndex = MathHelper.clamp(this.selection + ChatSuggestorGui.this.inWindowIndexOffset - ChatSuggestorGui.this.maxSuggestionSize, 0, Math.max(this.suggestions.size() - ChatSuggestorGui.this.maxSuggestionSize, 0));
            }

        }

        public void select(int index) {
            this.selection = index;
            if (this.selection < 0) {
                // Wrap to the top
                this.selection += this.suggestions.size();
            }

            if (this.selection >= this.suggestions.size()) {
                // Wrap to the bottom
                this.selection -= this.suggestions.size();
            }

            Suggestion suggestion = this.suggestions.get(this.selection);
            ChatSuggestorGui.this.textField.setSuggestion(ChatSuggestorGui.getSuggestionSuffix(ChatSuggestorGui.this.textField.getText(), suggestion.apply(this.typedText)));
            if (NarratorManager.INSTANCE.isActive() && this.lastNarrationIndex != this.selection) {
                NarratorManager.INSTANCE.narrate(this.getNarration());
            }

        }

        public void complete() {
            Suggestion suggestion = this.suggestions.get(this.selection);
            ChatSuggestorGui.this.completingSuggestions = true;
            ChatSuggestorGui.this.textField.setText(suggestion.apply(this.typedText));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            ChatSuggestorGui.this.textField.setSelectionStart(i);
            ChatSuggestorGui.this.textField.setSelectionEnd(i);
            this.select(this.selection);
            ChatSuggestorGui.this.completingSuggestions = false;
            this.completed = true;
        }

        private String getNarration() {
            this.lastNarrationIndex = this.selection;
            Suggestion suggestion = this.suggestions.get(this.selection);
            Message message = suggestion.getTooltip();
            return message != null ? I18n.translate("narration.suggestion.tooltip", this.selection + 1, this.suggestions.size(), suggestion.getText(), message.getString()) : I18n.translate("narration.suggestion", this.selection + 1, this.suggestions.size(), suggestion.getText());
        }

        public void discard() {
            ChatSuggestorGui.this.window = null;
        }
    }

}
