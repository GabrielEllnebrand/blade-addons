package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StormTickTimer {

    private static final Pattern PATTERN = Pattern.compile("^⚠ Storm is enraged! ⚠$");

    private static final long DEATH_DISPLAY_DURATION = 2000;

    @ConfigValue
    public static boolean enableStormTickTimer = false;

    @ConfigValue
    public static boolean enableStormDeathTime = false;

    private static int tick = 0;
    private static double deathTime = 0;
    private static long deathStartDisplayTime = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && !Phase.stormDead()) tick++;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
                deathTime = 0;
                deathStartDisplayTime = 0;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !Phase.inP2() || !enableStormDeathTime) return;

            Matcher matcher = PATTERN.matcher(message.getString());

            if (matcher.find()) {
                deathTime = (tick * Constants.TICK_DURATION);
                deathStartDisplayTime = System.currentTimeMillis();
                InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
                gameHud.getChatHud().addMessage(
                        Text.literal("Storm died at: ").formatted(Formatting.GREEN)
                                .append(Text.literal(Constants.DECIMAL_FORMAT.format(deathTime) + "s").formatted(Formatting.YELLOW))
                                .append(Text.literal(".").formatted(Formatting.GREEN))
                );
            }

        });
    }
    
    @ConfigValue
    public static HUDComponent stormTickTimer = new HUDComponent(0, 0, 30, 10, 1, "Storm Tick Timer",
            () -> enableStormTickTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * Constants.TICK_DURATION;

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, 0xffffffff, true);
            })
    );

    @ConfigValue
    public static HUDComponent stormDeathTime = new HUDComponent(0, 0, 30, 10, 1, "Storm Death Time",
            () -> enableStormTickTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead() && deathTime > 0 && deathStartDisplayTime > System.currentTimeMillis() - DEATH_DISPLAY_DURATION,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(deathTime)).formatted(Formatting.DARK_PURPLE), x, y, 0xffffffff, true);
            }), () -> enableStormTickTimer
    );
}
