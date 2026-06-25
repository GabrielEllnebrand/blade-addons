package blade.addon.utils;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.interfaces.GameHud;
import config.practical.data.SoundData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Misc {
    public static final Minecraft INSTANCE = Minecraft.getInstance();
    private static final Component ON = Component.literal("ON").withStyle(ChatFormatting.GREEN);
    private static final Component OFF = Component.literal("OFF").withStyle(ChatFormatting.RED);

    public static Vec3 getPos(Entity entity, double tickProgress) {
        double x = Mth.lerp(tickProgress, entity.xOld, entity.getX());
        double y = Mth.lerp(tickProgress, entity.yOld, entity.getY());
        double z = Mth.lerp(tickProgress, entity.zOld, entity.getZ());
        return new Vec3(x, y, z);
    }

    public static double getDistance(Entity e1, Entity e2) {
        return getDistance(e1.getX(), e1.getZ(), e2.getX(), e2.getZ());
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        return ((x1 - x2) * (x1 - x2)) + ((z1 - z2) * (z1 - z2));
    }

    public static void addChatMessage(Component text) {
        try {
            if (INSTANCE == null) return;
            Gui gameHud = INSTANCE.gui;
            ChatComponent hud = gameHud.getChat();
            forceMainThread(() -> hud.addClientSystemMessage(Component.literal(ExtraOptions.textPrefix).append(text)));
        } catch (IndexOutOfBoundsException ignored) {
            Debug.LOGGER.error("Chat message failed to get added");
        }
    }

    public static Component getStatusText(boolean status) {
        return status ? ON : OFF;
    }

    public static void setTitle(Component text) {
        forceMainThread(() -> INSTANCE.gui.setTitle(text));
    }

    public static void forceTitle(Component title, Component subtitle) {
        GameHud gameHud = (GameHud) INSTANCE.gui;
        forceMainThread(() -> gameHud.blade_addons$forceTitle(title, subtitle));
    }

    public static void executeCommand(String string) {
        ClientPacketListener networkHandler = INSTANCE.getConnection();
        if (networkHandler == null) return;
        forceMainThread(() -> networkHandler.sendCommand(string));
    }

    public static void sendSound(SoundEvent soundEvent, float volume, float pitch) {
        LocalPlayer player = INSTANCE.player;
        if (player == null) return;
        forceMainThread(() -> player.playSound(soundEvent, volume, pitch));
    }

    public static void sendSound(SoundData soundData) {
        sendSound(SoundEvent.createVariableRangeEvent(soundData.getSound()), soundData.getVolume(), soundData.getPitch());
    }

    public static void forceMainThread(Runnable runnable) {
        if (INSTANCE.isSameThread()) {
            runnable.run();
        } else {
            INSTANCE.executeIfPossible(runnable);
        }
    }
}
