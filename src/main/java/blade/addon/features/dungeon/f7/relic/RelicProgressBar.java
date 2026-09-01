package blade.addon.features.dungeon.f7.relic;

import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RelicProgressBar extends HUDComponent {

    public RelicProgressBar() {
        super("Relic progressbar");
    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Floor7.enableRelicStartTimer && Floor7.replaceWithProgressBar;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableRelicStartTimer && Location.inDungeon() && Phase.inP5() && RelicSpawn.getTick() > -1 && Floor7.replaceWithProgressBar;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P5);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int x = getScaledX();
        int y = getScaledY();
        int tick = RelicSpawn.getTick();

        //from valleyAddons
        StringBuilder message = new StringBuilder("§8[");

        if (Floor7.useValleyBar) {
            for (int i = 0; i < Floor7.relicSpawnTicks; ++i) {
                if (i < tick) {
                    if (tick > 2) message.append("§a|");
                    else message.append("§c|");
                } else message.append("§7|");
            }
            message.append("§8]");
        } else {
            int diff = Floor7.relicSpawnTicks - tick;
            for (int i = 0; i < Floor7.relicSpawnTicks; i++) {
                if (i < diff) {
                    if (tick < 2) message.append("§a|");
                    else message.append("§c|");
                } else message.append("§7|");
            }
            message.append("§8]");
        }

        RenderUtils.drawCenteredText(guiGraphicsExtractor, Minecraft.getInstance().font, Component.literal(message.toString()), x, y, getWidth(), 0xffffffff);

    }
}
