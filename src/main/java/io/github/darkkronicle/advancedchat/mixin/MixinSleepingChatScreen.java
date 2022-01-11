package io.github.darkkronicle.advancedchat.mixin;

import io.github.darkkronicle.advancedchat.gui.AdvancedSleepingChatScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SleepingChatScreen.class)
public class MixinSleepingChatScreen {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void sleepInit(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new AdvancedSleepingChatScreen());
        ci.cancel();
    }

}
