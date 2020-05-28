package darkkronicle.advancedchat.mixins;

import darkkronicle.advancedchat.gui.AdvancedChatScreen;
import darkkronicle.advancedchat.gui.ChatLogScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().openScreen(new ChatLogScreen());
    }

}
