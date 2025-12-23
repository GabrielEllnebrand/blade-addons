package blade.addon.features.dungeon.f7;

import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class Pre4Notifier {

    private static final long TOTAL_DURATION = 1500;

    private static long completedTime = 0;
    private static boolean showNotification = false;


    public static void init() {
        Events.ON_TERMINAL.register((name, objective) -> {
            if (!Floor7.notifyPre4Completion) return;

            if (objective.equals("device") && atDev()) {
                completedTime = System.currentTimeMillis();
                showNotification = true;
                Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);
            }
        });
    }

    private static boolean atDev() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return (player.getX() >= 63 && player.getX() <= 64 && player.getY() == 127 && player.getZ() >= 35 && player.getZ() <= 36);
    }

    public static boolean disableTitles() {
        return  Floor7.disableTitlesAtPre4 && atDev() && Phase.inTerminals();
    }

    public static boolean display() {
        return showNotification;
    }

    public static void render(HUDComponent component, DrawContext context) {
        if (System.currentTimeMillis() - completedTime >= TOTAL_DURATION) showNotification = false;

        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal("§aDevice Completed!"), x, y, component.getWidth());
    }
}
