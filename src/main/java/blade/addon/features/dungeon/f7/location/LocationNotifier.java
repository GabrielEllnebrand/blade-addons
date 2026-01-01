package blade.addon.features.dungeon.f7.location;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationNotifier {

    private static final Pattern PATTERN = Pattern.compile("^(At |Inside )");

    private static Text notification = Text.literal("§6Username is At Location!");
    private static int ticks = 0;

    public static void init() {
        Events.ON_PARTY_MESSAGE.register((username, message) -> {
            if (!Floor7.displayLocationNotification || !Location.inDungeon()) return false;
            Matcher matcher = PATTERN.matcher(message);
            if (!matcher.find()) return false;
            String action = matcher.group(1);

            if (Floor7.dontNotifiyForYourself && Misc.isClientPlayer(username)) return false;
            String screenNotification = (username + " is " + action + message.substring(action.length()) + "!").replaceAll("§.", "");
            if (PositionMessages.hasBeenSent(screenNotification)) return false;
            startNotification(screenNotification);

            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks = Math.max(ticks - 1, 0));
    }

    public static void startNotification(String message) {
        notification = Text.literal("§6"+message);
        ticks = Floor7.notificationDuration;

        for (int i = 0; i < Floor7.notificationRepetitions; i++) {
            Scheduler.scheduleSound(Floor7.atLocationSound, i + 1);
        }
    }

    public static boolean display() {
        return ticks > 0 && Floor7.displayLocationNotification;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, notification, x, y, component.getWidth());
    }
}
