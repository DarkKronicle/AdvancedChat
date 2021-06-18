package io.github.darkkronicle.advancedchat.filters.processors;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchat.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchat.util.FluidText;
import io.github.darkkronicle.advancedchat.util.SearchResult;
import io.github.darkkronicle.advancedchat.util.SearchUtils;
import io.github.darkkronicle.advancedchat.util.StringMatch;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.config.gui.widgets.WidgetLabelHoverable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.MessageType;

import java.util.List;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class CommandRunProcessor implements IMatchProcessor {

  private static String translate(String key) {
      return "advancedchat.config.processor.commandrun."  + key;
  }

  private final ConfigStorage.SaveableConfig<ConfigString> command = ConfigStorage.SaveableConfig.fromConfig("command",
          new ConfigString(translate("command"), "/say AdvancedChat!", translate("info.command")));

          @Override
          public Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
              String content = search.getGroupReplacements(command.config.getStringValue(), true);
              return Result.getFromBool(true);
          }

          public class SenderScreen extends GuiBase {

              private GuiTextFieldGeneric textField;

              @Override
              public void onClose() {
                  save();
                  super.onClose();
              }

              public SenderScreen(Screen parent) {
                  this.setParent(parent);
                  this.setTitle(StringUtils.translate("advancedchat.screen.command"));
              }

              @Override
              protected void closeGui(boolean showParent) {
                  save();
                  super.closeGui(showParent);
              }

              public void save() {
                  command.config.setValueFromString(textField.getText());
              }

              private int getWidth() {
                  return 300;
              }

              @Override
              public void initGui() {
                  super.initGui();
                  int x = 10;
                  int y = 26;
                  String name = CommandRunProcessor.ButtonListener.Type.BACK.getDisplayName();
                  int nameW = StringUtils.getStringWidth(name) + 10;
                  ButtonGeneric button = new ButtonGeneric(x, y, nameW, 20, name);
                  this.addButton(button, new ButtonListener(ButtonListener.Type.BACK, this));
                  y += 30;
                  textField = new GuiTextFieldGeneric(x, y, getWidth(), 20, MinecraftClient.getInstance().textRenderer);
                  textField.setMaxLength(64000);
                  textField.setText(command.config.getStringValue());
                  this.addTextField(textField, null);
              }

      public Result processMatches(FluidText text, FluidText unfiltered, SearchResult matches) {
          MinecraftClient client = MinecraftClient.getInstance();
          if (client.player == null) {
              return Result.PROCESSED;
          }

          String commandstr = command.config.getStringValue();
          if (!commandstr.startsWith("/")) {
            commandstr = "/" + commandstr;
          }
          client.player.sendChatMessage(commandstr);
          return Result.PROCESSED;
      }


    public void back() {
        this.closeGui(true);
    }

}

    public static class ButtonListener implements IButtonActionListener {

        private final SenderScreen parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, SenderScreen parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back"),
            ;
            private final String translation;

            private static String translate(String key) {
                return "advancedchat.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }

        }

    }

}
