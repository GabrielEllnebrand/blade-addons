package blade.addon.utils;

import blade.addon.mixin.ChatHudInvoker;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.interfaces.GameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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

    public static Vec3d getPos(Entity entity, double tickProgress) {
        double x = MathHelper.lerp(tickProgress, entity.lastRenderX, entity.getX());
        double y = MathHelper.lerp(tickProgress, entity.lastRenderY, entity.getY());
        double z = MathHelper.lerp(tickProgress, entity.lastRenderZ, entity.getZ());
        return new Vec3d(x, y, z);
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

    public static void forceMainThread(Runnable runnable) {
        if (INSTANCE.isOnThread()) {
            runnable.run();
        } else {
            INSTANCE.executeSync(runnable);
        }
    }

    public static String copyChat(ChatHudInvoker hudInvoker, double x, double y) {
        List<ChatHudLine.Visible> messages = hudInvoker.getVisibleMessages();
        int endIndex = hudInvoker.getLineIndex(x, y);

        if (messages == null || endIndex < 0 || endIndex >= messages.size()) return null;

        StringBuilder tempBuilder = new StringBuilder();

        int startIndex = endIndex;

        //find start of msg
        for (int i = endIndex; i >= 0; i--) {
            ChatHudLine.Visible chatHudLine = messages.get(i);
            if (chatHudLine.endOfEntry()) {
                startIndex = i;
                break;
            }
        }

        if (!messages.get(endIndex).endOfEntry()) {
            //find end of msg
            for (int i = endIndex + 1; i < messages.size(); i++) {
                ChatHudLine.Visible chatHudLine = messages.get(i);
                if (chatHudLine.endOfEntry()) {
                    endIndex = i - 1;
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = endIndex; i >= startIndex; i--) {
            ChatHudLine.Visible chatHudLine = messages.get(i);

            chatHudLine.content().accept((index, style, codePoint) -> {
                builder.appendCodePoint(codePoint);
                return true;
            });
        }
        return builder.toString();
    }
}
