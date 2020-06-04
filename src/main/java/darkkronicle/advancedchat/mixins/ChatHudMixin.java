package darkkronicle.advancedchat.mixins;

import darkkronicle.advancedchat.gui.AdvancedChatScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    public void isChatFocused(CallbackInfoReturnable<Boolean> ci) {
        // Used to see if ChatHud should render past messages.
        ci.setReturnValue(client.currentScreen instanceof AdvancedChatScreen || client.currentScreen instanceof ChatScreen);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(int ticks, CallbackInfo ci) {
        // Cancels normal ChatHud from being rendered.
        ci.cancel();
    }
}
