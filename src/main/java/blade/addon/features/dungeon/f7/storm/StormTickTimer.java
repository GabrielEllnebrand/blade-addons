package blade.addon.features.dungeon.f7.storm;

import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StormTickTimer {

    private static final Pattern PATTERN = Pattern.compile("^⚠ Storm is enraged! ⚠$");

    private static final long DEATH_DISPLAY_DURATION = 2000;
    private static final int WARN_TICK = 20 * 20;

    private static final int CRUSH_TICK = 31 * 20;
    private static final int COUNTDOWN_DURATION =  5 * 20;

    private static int tick = 0;
    private static double deathTime = 0;
    private static long deathStartDisplayTime = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && !Phase.stormDead()) tick++;

            if (tick == WARN_TICK && Floor7.notifyUsedSpiritMask && DungeonClass.isClass(DungeonClass.MAGE) && InvincibilityTimer.spiritMaskUsed()) {
                Misc.setTitle(Component.literal("Leap to arch"));
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
                deathTime = 0;
                deathStartDisplayTime = 0;
            }
            return false;
        });

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP2() || !Floor7.enableStormDeathTime) return false;

            Matcher matcher = PATTERN.matcher(text.getString());

            if (matcher.find()) {
                deathTime = (tick * Constants.TICK_DURATION);
                deathStartDisplayTime = System.currentTimeMillis();
                Misc.addChatMessage(Component.literal("§aStorm died at: §e" + Constants.DECIMAL_FORMAT.format(deathTime) + "s§a."));
            }

            return false;
        });
    }

    public static boolean display() {
        if (Floor7.tickDownStormTickTimer) {
            double diff = CRUSH_TICK - tick;
            if (diff > COUNTDOWN_DURATION || diff < 0) return false;
        }

        return Floor7.enableStormTickTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead();
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        double num = tick * Constants.TICK_DURATION;
        if (Floor7.tickDownStormTickTimer) {
            num = CRUSH_TICK * Constants.TICK_DURATION - num;
        }
        RenderUtils.drawTimer(component, graphics, num, Floor7.stormTickTimerColor);
    }

    public static boolean displayDeathTime() {
        return Floor7.enableStormDeathTime && Location.inDungeon() && Phase.inP2() && !Phase.stormDead() && deathTime > 0 && deathStartDisplayTime > System.currentTimeMillis() - DEATH_DISPLAY_DURATION;
    }

    public static void renderDeathTime(HUDComponent component, GuiGraphicsExtractor graphics) {
        RenderUtils.drawTimer(component, graphics, deathTime, Constants.DARK_PURPLE);
    }
}
