package blade.addon.features.dungeon.f7.maxor;

import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.TextUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MaxorStun extends HUDComponent {

    private static final int TOTAL_TICKS = 240;

    private int tick = 0;
    private boolean stunned = false;

    public MaxorStun() {
        super("Maxor stun display");
    }

    public void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP1())
                return false;
            String string = text.getString();

            if (string.equals("[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!") || string.equals("[BOSS] Maxor: YOU TRICKED ME!")) {
                stunned = true;
            } else if (string.equals("⚠ Maxor is enraged! ⚠")) {
                tick = TOTAL_TICKS;
                stunned = false;
            }

            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            if (stunned) {
                tick = Math.max(0, tick - 1);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            stunned = false;
            tick = TOTAL_TICKS;
            return false;
        });
    }

    @Override
    public int getWidth() {
        return 70;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Floor7.maxorStunDuration;
    }

    @Override
    public boolean shouldRender() {
        return tick > 0 && Location.inDungeon() && Phase.inP1() && Floor7.maxorStunDuration && stunned;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P1);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawPrefixedText(this, guiGraphicsExtractor, "Stunned", TextUtil.formatTicks(tick));

    }

}
