package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class DeathTickTimer {

    private static final int TOTAL_DEATH_TICKS = 40;
    private static Vec3d firstPosition;

    @ConfigValue
    public static boolean enableDeathTickTimer = false;
    private static int tick = TOTAL_DEATH_TICKS;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon()) {
                tick--;
                if (tick <= 0) tick = TOTAL_DEATH_TICKS;
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

    public static void reset() {
        tick = 0;
        firstPosition = null;
    }

    @ConfigValue
    public static HUDComponent deathTickTimer = new HUDComponent(0, 0, 30, 10, 1, "Death Tick Timer",
            () -> enableDeathTickTimer && Location.inDungeon(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * Constants.TICK_DURATION;

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, 0xffffffff, true);
            })
    );
}
