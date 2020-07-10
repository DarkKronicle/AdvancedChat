package net.darkkronicle.advancedchat.mixin;

import net.darkkronicle.advancedchat.storage.Filter;
import net.darkkronicle.advancedchat.util.SearchText;
import net.darkkronicle.advancedchat.util.SplitText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class MixinChatHud {


    @Shadow
    private void addMessage(StringRenderable message, int messageId, int timestamp, boolean bl) {}

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    public void addMessage(Text message, CallbackInfo ci) {
        SplitText text = new SplitText(message);
        Optional<List<SearchText.StringMatch>> omatches = SearchText.findMatches(text.getFullMessage(), "Gave 1 [Command", Filter.FindType.LITERAL);
        if (!omatches.isPresent()) {
            return;
        }
        List<SearchText.StringMatch> matches = omatches.get();
        text.replaceStrings(matches, "SCREAMING");
        System.out.println(text.getFullMessage());
        addMessage(text.getStringRenderable(), 0, MinecraftClient.getInstance().inGameHud.getTicks(), false);

    }
}
