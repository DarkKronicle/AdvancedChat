package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class NotifyFilter extends Filter {

    @Override
    public void reloadFilters() {

    }

    @Override
    public FilteredMessage filter(String message, ConfigFilter filter) {
        if (!filter.isActive()) {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
        }
        if (!filter.isRegex()) {
            if (filter.isIgnoreCase()) {
                if (message.toLowerCase().contains(filter.getTrigger().toLowerCase())) {
                    if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                        notifyPlayer();
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                    } else {
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                    }
                }
            } else {
                if (message.contains(filter.getTrigger())) {
                    if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                        notifyPlayer();
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                    } else {
                        return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                    }
                }
            }
        } else {
            Pattern pattern = Pattern.compile(filter.getTrigger());
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                if (filter.getNotifyType() == ConfigFilter.NotifyType.SOUND) {
                    notifyPlayer();
                    return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.NOTIFY);
                } else {
                    return new FilteredMessage(message, true, false, FilteredMessage.FilterResult.BANNER);
                }
            }

        }
        return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);
    }

    @Override
    public FilteredMessage.FilterResult filterType() {
        return FilteredMessage.FilterResult.NOTIFY;
    }

    public void notifyPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1));
    }
}
