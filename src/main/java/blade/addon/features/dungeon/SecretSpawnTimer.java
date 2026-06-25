package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecretSpawnTimer {

    private static final Pattern PATTERN = Pattern.compile("^Time Elapsed: ");
    private static final int TICKS_PER_SECOND = 20;

    private static int tick = 0;

    public static void init() {

        Events.ON_TEAM.register(text -> {
            if (!Location.inDungeon() || !Dungeons.enableSecretSpawnTimer) return false;
            Matcher matcher = PATTERN.matcher(text);
            if (matcher.find()) {
                tick = TICKS_PER_SECOND;
            }
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });
    }

    public static boolean display() {
        return Location.inDungeon() && Dungeons.enableSecretSpawnTimer && !Phase.inBoss() && Phase.runStarted();
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        int color = tick > 10 ? Constants.GREEN : tick > 5 ? Constants.GOLD : Constants.RED;

        RenderUtils.drawCenteredText(context, Minecraft.getInstance().font, Component.literal(tick + ""), x, y, component.getWidth(), color);

    }
}
