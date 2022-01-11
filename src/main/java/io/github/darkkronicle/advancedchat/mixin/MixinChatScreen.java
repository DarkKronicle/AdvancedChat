package io.github.darkkronicle.advancedchat.mixin;

import io.github.darkkronicle.advancedchat.gui.AdvancedChatScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow private String originalChatText;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void chatInit(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new AdvancedChatScreen(this.originalChatText));
        ci.cancel();
    }

    @Inject(method = "addScreenNarrations", at = @At("HEAD"), cancellable = true)
    public void screenNarrations(NarrationMessageBuilder builder, CallbackInfo ci) {
        ci.cancel();
    }

}
