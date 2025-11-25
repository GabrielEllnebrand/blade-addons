package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class DistanceToLedge {

    private static final double MIN_X = 33.704;
    private static final int WIDTH = 30;

    @ConfigValue
    public static boolean displayDistanceToLedge = false;

    public static void init() {

    }

    public static double getDistance() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;

        return player.getX() - MIN_X;
    }

    @ConfigValue
    public static HUDComponent distanceToLedgeComponent = new HUDComponent(0, 0, WIDTH, 10, 1, "Distance to ledge",
            () -> {
                if (!displayDistanceToLedge || !Location.inDungeon()) return false;

                return Phase.inP2() && !Phase.stormDead() && (DungeonClass.isClass(DungeonClass.MAGE) || DungeonClass.isClass(DungeonClass.ARCHER));
            },
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                RenderUtils.drawCenteredText(drawContext, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(getDistance())), x, y, WIDTH);
            }), () -> displayDistanceToLedge
    );
}
