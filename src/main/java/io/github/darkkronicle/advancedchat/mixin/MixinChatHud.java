package io.github.darkkronicle.advancedchat.mixin;

import io.github.darkkronicle.advancedchat.chat.MessageDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class, priority = 1050)
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

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text text, int id, CallbackInfo ci) {
        MessageDispatcher.getInstance().handleText(text);
        ci.cancel();
    }

}