package net.darkkronicle.advancedchat.mixin;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
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

    @Inject(method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        AdvancedChat.getAdvancedChatHud().addMessage(stringRenderable, messageId, timestamp, bl);
        ci.cancel();
    }

}