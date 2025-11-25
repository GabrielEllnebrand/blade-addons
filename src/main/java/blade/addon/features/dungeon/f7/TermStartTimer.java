package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class TermStartTimer {

    private static final int WIDTH = 30;
    private static final int TOTAL_TICKS = 100;

    @ConfigValue
    public static boolean enableTermStartTimer = false;
    private static int tick = 100;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && Phase.stormDead()) tick--;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = TOTAL_TICKS;
            }
        });
    }
    
    @ConfigValue
    public static HUDComponent termStartTimer = new HUDComponent(0, 0, WIDTH, 10, 1, "Term Start Timer",
            () -> enableTermStartTimer && Location.inDungeon() && Phase.inP2() && Phase.stormDead(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * Constants.TICK_DURATION;
                RenderUtils.drawCenteredText(drawContext, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, WIDTH, 0xFFFFFF55);

            }), () -> enableTermStartTimer
    );
}
