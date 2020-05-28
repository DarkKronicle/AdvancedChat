package darkkronicle.advancedchat.mixins;

import darkkronicle.advancedchat.AdvancedChatClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "getChatHud", at = @At("HEAD"), cancellable = true)
    public void getChatHud(CallbackInfoReturnable<ChatHud> ci) {
        ci.setReturnValue(AdvancedChatClient.getChatHud());
    }

}
