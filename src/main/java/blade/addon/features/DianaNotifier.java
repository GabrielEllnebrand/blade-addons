package blade.addon.features;

import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

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
            String string = message.getString();
            String lowerCase = string.toLowerCase();
            if (!lowerCase.contains("you dug out a")) return;
            if (checkHarpy) {
                check(lowerCase, "harpy");
            }

            if (checkBull) {
                check(lowerCase, "cretan bull");

            }

            if (checkNymph) {
                check(lowerCase, "stranded nymph");
            }
        });
    }

    private static void check(String string, String name) {
        if (string.contains(name)) {
            MinecraftClient client = MinecraftClient.getInstance();
            Misc.setTitle(Text.literal(name));
            ClientPlayerEntity player = client.player;
            if (player == null) {
                Misc.addChatMessage(Text.literal("Player is somehow null"));
                return;
            }

            if (sendSound) {
                Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);

            }

            if (sendWaypoint) {
                Scheduler.scheduleCommand("pc x: " + (int) player.getX() + ", y: " + (int) player.getY() + ", z: " + (int) player.getZ());
            }
        }
    }
}
