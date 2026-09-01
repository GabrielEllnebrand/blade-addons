package blade.addon.features.dungeon.f7.storm;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StormDeathTime extends HUDComponent {

    private static final Pattern PATTERN = Pattern.compile("^⚠ Storm is enraged! ⚠$");

    private static final long DEATH_DISPLAY_DURATION = 2000;

    private static double deathTime = 0;
    private static long deathStartDisplayTime = 0;

    public StormDeathTime() {
        super("Storm death time");
    }

    public void init() {

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                deathTime = 0;
                deathStartDisplayTime = 0;
            }
            return false;
        });

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP2() || !Floor7.enableStormDeathTime) return false;

            Matcher matcher = PATTERN.matcher(text.getString());

            if (matcher.find()) {
                deathTime = (StormTime.getTick() * Constants.TICK_DURATION);
                deathStartDisplayTime = System.currentTimeMillis();
                Misc.addChatMessage(Component.literal("§aStorm died at: §e" + Constants.DECIMAL_FORMAT.format(deathTime) + "s§a."));
            }

            return false;
        });
    }

    @Override
    public int getWidth() {
        return 30;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Floor7.enableStormDeathTime;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableStormDeathTime && Location.inDungeon() && Phase.inP2() && !Phase.stormDead() && deathTime > 0 && deathStartDisplayTime > System.currentTimeMillis() - DEATH_DISPLAY_DURATION;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, deathTime, Constants.DARK_PURPLE);
    }
}
