package blade.addon.features.other;

import blade.addon.utils.debug.Debug;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class DianaNotifier {

    @ConfigValue
    public static boolean sendSound = false;
    @ConfigValue
    public static boolean sendWaypoint = false;
    @ConfigValue
    public static boolean checkHarpy = false;
    @ConfigValue
    public static boolean checkBull = false;
    @ConfigValue
    public static boolean checkNymph = false;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(message-> {
            if (!Location.in(Location.HUB)) return false;
            String string = message.getString();
            String lowerCase = string.toLowerCase();
            if (!lowerCase.contains("you dug out a")) return false;
            if (checkHarpy) {
                check(lowerCase, "harpy");
            }

            if (checkBull) {
                check(lowerCase, "cretan bull");

            }

            if (checkNymph) {
                check(lowerCase, "stranded nymph");
            }

            return false;
        });
    }

    private static void check(String string, String name) {
        if (string.contains(name)) {
            Minecraft client = Minecraft.getInstance();
            Misc.setTitle(Component.literal(name));
            LocalPlayer player = client.player;
            if (player == null) {
                Debug.sendDebugMessage(Component.literal("Player is null when checking diana message"));
                return;
            }

            if (sendSound) {
                Scheduler.scheduleSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2, 1);

            }

            if (sendWaypoint) {
                Scheduler.scheduleCommand("pc x: " + (int) player.getX() + ", y: " + (int) player.getY() + ", z: " + (int) player.getZ());
            }
        }
    }
}
