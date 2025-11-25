package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrystalSpawn {

    private static final Pattern PATTERN_1 = Pattern.compile("^\\[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!$");
    private static final Pattern PATTERN_2 = Pattern.compile("^\\[BOSS] Maxor: YOU TRICKED ME!$");

    @ConfigValue
    public static boolean enableCrystalSpawnTime = false;

    private static final int WIDTH = 30;

    private static final int TICK_SPAWN = 34;
    private static int tick = 0;

    public static void init() {

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !Phase.inP1() || !enableCrystalSpawnTime) return;

            Matcher matcher = PATTERN_1.matcher(message.getString());
            if (!matcher.find()) {
                matcher = PATTERN_2.matcher(message.getString());

                if (!matcher.find()) return;
            }

            tick = TICK_SPAWN;
        });


        Events.ON_SERVER_TICK.register(() -> tick = Math.max(tick - 1, 0));

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
        });

    }

    @ConfigValue
    public static HUDComponent crystalSpawnTime = new HUDComponent(0, 0, WIDTH, 10, 1, "Crystal Spawn Time",
            () -> tick > 0 && Location.inDungeon(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * Constants.TICK_DURATION;

                RenderUtils.drawCenteredText(drawContext, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)).formatted(Formatting.LIGHT_PURPLE), x, y, WIDTH);

            }), () -> enableCrystalSpawnTime
    );
}
