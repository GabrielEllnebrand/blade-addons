package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;

public class StormTickTimer {
    @ConfigValue
    public static boolean enableStormTickTimer = false;
    private static int tick = 0;

    public static void tick(MinecraftClient client) {
        if (Location.inDungeon() && Phase.inP2()) tick++;
    }

    @ConfigValue
    public static HUDComponent stormTickTimer = new HUDComponent(0, 0, 30, 10, 1,
            () -> enableStormTickTimer && Location.inDungeon() && Phase.inP2(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * Constants.TICK_DURATION;

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, 0xffffffff, true);
            })
    );
}
