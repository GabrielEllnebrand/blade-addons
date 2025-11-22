package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class DistanceToLedge {

    @ConfigValue
    public static boolean displayDistanceToLedge = false;

    private static final double MIN_X = 33.704;

    public static void init() {

    }

    public static double getDistance() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;

       return player.getX() - MIN_X;
    }

    @ConfigValue
    public static HUDComponent distanceToLedgeComponent = new HUDComponent(0, 0, 30, 10, 1, "Distance to ledge",
            () -> displayDistanceToLedge && Location.inDungeon() && Phase.inP2() && (DungeonClass.isClass(DungeonClass.MAGE) || DungeonClass.isClass(DungeonClass.ARCHER)),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(getDistance())), x, y, 0xffffffff, true);
            }), () -> displayDistanceToLedge
    );
}
