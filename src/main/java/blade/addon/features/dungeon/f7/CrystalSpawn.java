package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrystalSpawn {

    private static final Pattern PATTERN_1 = Pattern.compile("^\\[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!$");
    private static final Pattern PATTERN_2 = Pattern.compile("^\\[BOSS] Maxor: YOU TRICKED ME!$");

    private static final int TICK_SPAWN = 34;
    private static int tick = 0;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP1() || !Floor7.enableCrystalSpawnTime) return;

            Matcher matcher = PATTERN_1.matcher(text.getString());
            if (!matcher.find()) {
                matcher = PATTERN_2.matcher(text.getString());

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

    public static boolean display() {
        return  tick > 0 && Location.inDungeon() && Floor7.enableCrystalSpawnTime;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        double num = tick * Constants.TICK_DURATION;

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)).formatted(Formatting.LIGHT_PURPLE), x, y, component.getWidth());
    }
}
