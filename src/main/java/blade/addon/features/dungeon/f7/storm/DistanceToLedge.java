package blade.addon.features.dungeon.f7.storm;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;

public class DistanceToLedge {

    private static final AABB YELLOW_PAD = new AABB(20, 163, 0, 58, 213, 107);
    private static final double MIN_X = 33.704;

    public static void init() {

    }

    public static double getDistance() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        return player.getX() - MIN_X;
    }

    private static boolean isAtYellow() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        return YELLOW_PAD.contains(player.position());
    }

    public static boolean display() {
        if (!Floor7.displayDistanceToLedge || !Location.inDungeon()) return false;
        if (Floor7.showDistanceAtYellowOnly && !isAtYellow()) return false;
        return Phase.inP2() && !Phase.stormDead() && (DungeonClass.isClass(DungeonClass.MAGE) || DungeonClass.isClass(DungeonClass.ARCHER));
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        RenderUtils.drawTimer(component, graphics, getDistance(), 0xffffffff);
    }
}
