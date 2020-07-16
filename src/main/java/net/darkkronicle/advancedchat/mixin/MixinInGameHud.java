package net.darkkronicle.advancedchat.mixin;

import net.darkkronicle.advancedchat.AdvancedChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Shadow private int ticks;

    @Inject(method = "getChatHud", at = @At("HEAD"), cancellable = true)
    public void getAdvancedChatHud(CallbackInfoReturnable<ChatHud> ci) {
   //     ci.setReturnValue(AdvancedChat.getAdvancedChatHud());
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void hudRender(MatrixStack matrixStack, float f, CallbackInfo ci) {
        AdvancedChat.getAdvancedChatHud().render(matrixStack, this.ticks);
    }
}
