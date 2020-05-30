package darkkronicle.advancedchat.mixins;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.gui.AdvancedChatScreen;
import darkkronicle.advancedchat.gui.ChatLogScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;

    /*@Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    private void addMessage(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        //AdvancedChatClient.chatHud.addMessage(message, messageId);
    }*/

    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    public void isChatFocused(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(client.currentScreen instanceof AdvancedChatScreen || client.currentScreen instanceof ChatScreen);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(int ticks, CallbackInfo ci) {
        ci.cancel();
    }
}
