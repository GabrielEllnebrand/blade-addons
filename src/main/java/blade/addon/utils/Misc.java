package blade.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Misc {
    public static final MinecraftClient instance = MinecraftClient.getInstance();

    public static boolean isClientPlayer(PlayerEntity entity) {
        ClientPlayerEntity clientPlayer = instance.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static void addChatMessage(Text text) {
        InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
        gameHud.getChatHud().addMessage(text);
    }

    public static void setTitle(Text text) {
        MinecraftClient.getInstance().inGameHud.setTitle(text);
    }

    public static void executeCommand(String string) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;
        networkHandler.sendChatCommand(string);
    }
}
