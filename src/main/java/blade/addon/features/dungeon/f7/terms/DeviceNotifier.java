package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DeviceNotifier {

    private static final long TOTAL_DURATION = 1500;

    private static final Vec3d SS_POSITION = new Vec3d(108, 119, 94);
    private static final double DISTANCE = 3;

    private static long completedTime = 0;
    private static boolean showNotification = false;


    public static void init() {
        Events.ON_TERMINAL.register((name, action, objective, current, total) -> {
            if (!objective.equals("device") || !EntityUtil.isClientPlayer(name)) return false;

            if ((Floor7.notifyPre4Completion && at4thDev()) || (Floor7.notifySSCompletion && atSS())) {
                completedTime = System.currentTimeMillis();
                showNotification = true;
                Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);
            }

            return false;
        });
    }

    public static boolean atSS() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return SS_POSITION.distanceTo(player.getEntityPos()) <= DISTANCE;
    }

    public static boolean atSS(PlayerEntity player) {
        return SS_POSITION.distanceTo(player.getEntityPos()) <= DISTANCE;
    }

    public static boolean at4thDev() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return (player.getX() >= 63 && player.getX() <= 64 && player.getY() == 127 && player.getZ() >= 35 && player.getZ() <= 36);
    }

    public static boolean disableTitles(Text title) {
        if (!(((Floor7.disableTitlesAtPre4 && at4thDev()) || (Floor7.disableTitlesAtSS && atSS())) && Phase.inTerminals()))
            return false;

        String string = title.getString();

        if (string.equals("§eYou became a ghost!") || string.equals("§7Hopefully your teammates will be able to revive you!") || string.equals("§e§lBEING REVIVED")) {
            return false;
        }

        return (!string.matches("§aYou will be revived in \\ds"));

    }

    public static boolean display() {
        return showNotification;
    }

    public static void render(HUDComponent component, DrawContext context) {
        if (System.currentTimeMillis() - completedTime >= TOTAL_DURATION) showNotification = false;
        RenderUtils.drawCenteredText(context, component, Text.literal("§aDevice Completed!"));
    }
}
