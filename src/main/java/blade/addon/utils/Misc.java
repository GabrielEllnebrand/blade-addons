package blade.addon.utils;

import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Misc {
    public static final MinecraftClient instance = MinecraftClient.getInstance();
    private static final Text ON = Text.literal("ON").formatted(Formatting.GREEN);
    private static final Text OFF = Text.literal("OFF").formatted(Formatting.RED);

    public static boolean isClientPlayer(PlayerEntity entity) {
        ClientPlayerEntity clientPlayer = instance.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static boolean isClientPlayer(Entity entity) {
        ClientPlayerEntity clientPlayer = instance.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static void addChatMessage(Text text) {
        try {
            InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
            gameHud.getChatHud().addMessage(Text.literal(ExtraOptions.textPrefix).append(text));
        } catch (IndexOutOfBoundsException ignored) {
            Debug.LOGGER.error("Chat message failed to get added");
        }
    }

    public static Text getStatusText(boolean status) {
        return status ? ON : OFF;
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
