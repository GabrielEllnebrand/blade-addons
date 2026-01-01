package blade.addon.utils;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.interfaces.GameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class Misc {
    public static final MinecraftClient INSTANCE = MinecraftClient.getInstance();
    private static final Text ON = Text.literal("ON").formatted(Formatting.GREEN);
    private static final Text OFF = Text.literal("OFF").formatted(Formatting.RED);

    public static boolean isClientPlayer(PlayerEntity entity) {
        ClientPlayerEntity clientPlayer = INSTANCE.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static boolean isClientPlayer(Entity entity) {
        ClientPlayerEntity clientPlayer = INSTANCE.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static boolean isClientPlayer(String name) {
        ClientPlayerEntity clientPlayer = INSTANCE.player;
        if (clientPlayer == null) return false;
        return clientPlayer.getName().getString().equals(name);
    }

    public static double getDistance(Entity e1, Entity e2) {
        return getDistance(e1.getX(), e1.getZ(), e2.getX(), e2.getZ());
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        return ((x1 - x2) * (x1 - x2)) + ((z1 - z2) * (z1 - z2));
    }

    public static void addChatMessage(Text text) {
        try {
            if (INSTANCE == null) return;
            InGameHud gameHud = INSTANCE.inGameHud;
            ChatHud hud = gameHud.getChatHud();
            forceMainThread(() -> hud.addMessage(Text.literal(ExtraOptions.textPrefix).append(text)));
        } catch (IndexOutOfBoundsException ignored) {
            Debug.LOGGER.error("Chat message failed to get added");
        }
    }

    public static Text getStatusText(boolean status) {
        return status ? ON : OFF;
    }

    public static void setTitle(Text text) {
        forceMainThread(() -> INSTANCE.inGameHud.setTitle(text));
    }

    public static void forceTitle(Text title, Text subtitle) {
        GameHud gameHud = (GameHud) INSTANCE.inGameHud;
        forceMainThread(() -> gameHud.blade_addons$forceTitle(title, subtitle));
    }

    public static void executeCommand(String string) {
        ClientPlayNetworkHandler networkHandler = INSTANCE.getNetworkHandler();
        if (networkHandler == null) return;
        forceMainThread(() -> networkHandler.sendChatCommand(string));
    }

    public static boolean containsLore(ItemStack item, String match) {
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null) return false;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for (Text line : lines.reversed()) {
            String string = line.getString();
            if (string.contains(match)) {
                return true;
            }
        }
        return false;
    }

    private static void forceMainThread(Runnable runnable) {
        if (INSTANCE.isOnThread()) {
            runnable.run();
        } else {
            INSTANCE.executeSync(runnable);
        }
    }
}
