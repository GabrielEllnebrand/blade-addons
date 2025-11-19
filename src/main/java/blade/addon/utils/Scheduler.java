package blade.addon.utils;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;

public class Scheduler {

    private static Screen scheduledScreen = null;
    private static int screenTicks = 0;

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
            if (scheduledScreen != null) {
                screenTicks--;
                if (screenTicks <= 0) {
                    minecraftClient.setScreen(scheduledScreen);
                    scheduledScreen = null;
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
}
