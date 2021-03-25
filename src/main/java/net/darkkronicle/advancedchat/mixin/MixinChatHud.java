package net.darkkronicle.advancedchat.mixin;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.chat.MessageDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class MixinChatHud {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, int ticks, CallbackInfo ci) {
        // Cancels normal ChatHud from being rendered.
        ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text text, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text text, CallbackInfo ci) {
        MessageDispatcher.getInstance().handleText(text);
        ci.cancel();
    }

}