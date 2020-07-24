package net.darkkronicle.advancedchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.TranslatableText;

public class AdvancedSleepingChatScreen extends AdvancedChatScreen {
    public AdvancedSleepingChatScreen() {
        super("");
    }

    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("multiplayer.stopSleeping").getString(), (buttonWidget) -> {
            this.stopSleeping();
        }));
    }

    public void onClose() {
        this.stopSleeping();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (keyCode == 256) {
            this.stopSleeping();
        } else if (keyCode == 257 || keyCode == 335) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }

            this.chatField.setText("");
            client.inGameHud.getChatHud().resetScroll();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void stopSleeping() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
        clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
        client.openScreen(null);
    }
}
