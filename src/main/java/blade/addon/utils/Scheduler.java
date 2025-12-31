package blade.addon.utils;

import com.mojang.brigadier.Command;
import config.practical.data.SoundData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;

import java.util.concurrent.CopyOnWriteArrayList;

public class Scheduler {

    private static Screen scheduledScreen = null;
    private static int screenTicks = 0;
    private static String scheduledCommand = null;

    static class Task {
        Runnable task;
        int delay;

        public Task(Runnable task, int delay) {
            this.task = task;
            this.delay = delay;
        }
    }

    private static final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
            if (scheduledScreen != null) {
                screenTicks--;
                if (screenTicks <= 0) {
                    minecraftClient.setScreen(scheduledScreen);
                    scheduledScreen = null;
                }
            }

            if (scheduledCommand != null) {
                ClientPlayerEntity player = minecraftClient.player;
                if (player != null && player.networkHandler != null) {
                    player.networkHandler.sendChatCommand(scheduledCommand);
                    scheduledCommand = null;
                }
            }

            for (int i = tasks.size() - 1; i >= 0; i--) {
                Task task = tasks.get(i);
                task.delay--;
                if (task.delay <= 0) {
                    minecraftClient.execute(task.task);
                    tasks.remove(i);
                }
            }

        });
    }

    public static int scheduleScreen(Screen screen) {
        if (screen == null) return -1;
        scheduledScreen = screen;
        screenTicks = 1;
        return Command.SINGLE_SUCCESS;
    }

    public static void scheduleSound(SoundEvent soundEvent, float volume, float pitch, int delay) {
        tasks.add(new Task(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            player.playSound(soundEvent, volume, pitch);
        }, delay));
    }

    public static void scheduleSound(SoundEvent soundEvent, float volume, float pitch) {
        scheduleSound(soundEvent, volume, pitch, 1);
    }


    public static void scheduleSound(SoundData soundData, int tick) {
        scheduleSound(SoundEvent.of(soundData.getSound()), soundData.getVolume(), soundData.getPitch(), tick);
    }

    public static void scheduleSound(SoundData soundData) {
       scheduleSound(soundData, 1);
    }

    public static void scheduleCommand(String command) {
        scheduledCommand = command;
    }

    public static void scheduleTask(Runnable runnable, int ticks) {
        tasks.add(new Task(runnable, ticks));
    }
}
