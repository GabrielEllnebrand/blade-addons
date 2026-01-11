package blade.addon.utils;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.interfaces.GameHud;
import config.practical.data.SoundData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Misc {
    public static final MinecraftClient INSTANCE = MinecraftClient.getInstance();
    private static final Text ON = Text.literal("ON").formatted(Formatting.GREEN);
    private static final Text OFF = Text.literal("OFF").formatted(Formatting.RED);

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

    public static void sendSound(SoundEvent soundEvent, float volume, float pitch) {
       ClientPlayerEntity player = INSTANCE.player;
        if (player == null) return;
        forceMainThread(() -> player.playSound(soundEvent, volume, pitch));
    }

    public static void sendSound(SoundData soundData) {
        sendSound(SoundEvent.of(soundData.getSound()), soundData.getVolume(), soundData.getPitch());
    }
        public static void forceMainThread(Runnable runnable) {
        if (INSTANCE.isOnThread()) {
            runnable.run();
        } else {
            INSTANCE.executeSync(runnable);
        }
    }
}
