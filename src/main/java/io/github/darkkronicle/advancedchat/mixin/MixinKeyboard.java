package io.github.darkkronicle.advancedchat.mixin;

import io.github.darkkronicle.advancedchat.AdvancedChat;
import io.github.darkkronicle.advancedchat.gui.AdvancedChatHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    public void processF3Chat(int key, CallbackInfoReturnable<Boolean> ci) {
        AdvancedChatHud.getInstance().clear();
    }
}
