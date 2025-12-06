package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DeathTickTimer {

    private static final int TOTAL_DEATH_TICKS = 40;
    private static Vec3d firstPosition;

    private static int tick = TOTAL_DEATH_TICKS;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon()) {
                tick--;
                if (tick <= 0) tick = TOTAL_DEATH_TICKS;
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
                firstPosition = null;
            }
        });
    }

    public static void onTeleport(Vec3d position) {
        if (Location.inDungeon()) {
            if (firstPosition == null) {
                firstPosition = position;
            }

            else if (position.x == firstPosition.x && position.y == firstPosition.y && position.z == firstPosition.z) {
                tick = TOTAL_DEATH_TICKS;
            }
        }
    }

    public static boolean display() {
        return Dungeons.enableDeathTickTimer && Location.inDungeon();
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        double num = tick * Constants.TICK_DURATION;
        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, component.getWidth());

    }
}
