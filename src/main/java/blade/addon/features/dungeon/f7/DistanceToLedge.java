package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class DistanceToLedge {

    private static final Box YELLOW_PAD = new Box(20, 163, 0, 58, 213, 107);
    private static final double MIN_X = 33.704;

    public static void init() {

    }

    public static double getDistance() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;

        return player.getX() - MIN_X;
    }

    private static boolean isAtYellow() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return YELLOW_PAD.contains(player.getEntityPos());
    }

    public static boolean display() {
        if (!Floor7.displayDistanceToLedge || !Location.inDungeon()) return false;
        if (Floor7.showDistanceAtYellowOnly && !isAtYellow()) return false;
        return Phase.inP2() && !Phase.stormDead() && (DungeonClass.isClass(DungeonClass.MAGE) || DungeonClass.isClass(DungeonClass.ARCHER));
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(getDistance())), x, y, component.getWidth());
    }
}
