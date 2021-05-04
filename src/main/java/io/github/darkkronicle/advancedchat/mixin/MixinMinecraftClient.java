package io.github.darkkronicle.advancedchat.mixin;

import io.github.darkkronicle.advancedchat.chat.ChatHistory;
import io.github.darkkronicle.advancedchat.config.ConfigStorage;
import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void onDisconnect(Screen screen, CallbackInfo ci) {
        if (ConfigStorage.General.CLEAR_ON_DISCONNECT.config.getBooleanValue()) {
            ChatHistory.getInstance().clear();
        }
    }
}
