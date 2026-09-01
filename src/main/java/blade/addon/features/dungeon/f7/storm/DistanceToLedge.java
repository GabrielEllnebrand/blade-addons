package blade.addon.features.dungeon.f7.storm;

import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DistanceToLedge extends HUDComponent {

    private static final AABB YELLOW_PAD = new AABB(20, 163, 0, 58, 213, 107);
    private static final double MIN_X = 33.704;

    public DistanceToLedge() {
        super("Distance to ledge");
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
        return Floor7.displayDistanceToLedge;
    }

    @Override
    public boolean shouldRender() {
        if (!Floor7.displayDistanceToLedge || !Location.inDungeon()) return false;
        if (Floor7.showDistanceAtYellowOnly && !isAtYellow()) return false;
        return Phase.inP2() && !Phase.stormDead() && isPYClass();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, getDistance(), 0xffffffff);
    }

    private double getDistance() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        return player.getX() - MIN_X;
    }

    private boolean isAtYellow() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        return YELLOW_PAD.contains(player.position());
    }

    private boolean isPYClass() {
        if (Floor7.displayDistanceOnAllClasses) return true;
        if (Phase.isInMasterMode()) {
            return DungeonClass.isClass(DungeonClass.MAGE) || DungeonClass.isClass(DungeonClass.ARCHER);
        } else {
            return DungeonClass.isClass(DungeonClass.TANK) || DungeonClass.isClass(DungeonClass.ARCHER);
        }
    }
}
