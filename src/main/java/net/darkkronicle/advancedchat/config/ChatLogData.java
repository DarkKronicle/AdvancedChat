package net.darkkronicle.advancedchat.config;

import lombok.Data;
import net.darkkronicle.advancedchat.chat.ChatLogMessage;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
@Data
public class ChatLogData {
    private ArrayList<ChatLogMessage> messages = new ArrayList<>();
    private boolean chatLogTime = ConfigStorage.ChatLogConfig.SHOW_TIME.config.getBooleanValue();
    private boolean chatHudTime = ConfigStorage.ChatScreen.SHOW_TIME.config.getBooleanValue();

    public void addMessage(ChatLogMessage message) {
        boolean showtime = ConfigStorage.ChatLogConfig.SHOW_TIME.config.getBooleanValue();
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.General.TIME_FORMAT.config.getStringValue());
            SplitText split = new SplitText(message.getOriginalText());
            split.addTime(format, message.getTime());
            message.setDisplayText(split.getText(), 600);
        }

        messages.add(message);

        int visibleMessagesMaxSize = ConfigStorage.ChatScreen.STORED_LINES.config.getIntegerValue();
        while(this.messages.size() > visibleMessagesMaxSize) {
            this.messages.remove(this.messages.size() - 1);
        }

    }


}
