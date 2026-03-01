package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Ported from valley with minimal changes
 */

public class LeapNotification {

    private static final Box[] REGIONS = {
            new Box(91, 105, 46, 111, 127, 123),
            new Box(17, 106, 121, 108, 145, 143),
            new Box(-2, 106, 51, 20, 145, 142),
            new Box(-1, 26, 29, 191, 145, 58),
            new Box(3, 5, 0, 128, 48, 140)
    };

    private static final Box RED_PILLAR_BOX = Box.of(new Vec3d(100, 116, 46), 2, 2, 2);
    private static final Box SS_BOX = new Box(107, 119, 92, 108, 121, 95);
    private static final Box EE2_BOX = new Box(57, 108, 130, 59, 110, 132);
    private static final Box HEE2_BOX = new Box(59, 132, 138, 62, 133, 140);
    private static final Box EE3_BOX = new Box(1, 108, 103, 3, 110, 105);
    private static final Box CORE_BOX = new Box(53.5, 114, 49.5, 55.5, 116, 51.5);
    private static final Box RELIC_BOX = new Box(51.5, 3, 73.5, 57.5, 8, 79.5);


    private static int count = 0;
    private static int currentSpot = -1;
    private static boolean inBounds = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!Floor7.leapNotifications || !Phase.isInFloor7() || !Location.inDungeon() || !Phase.inBoss()) {
                inBounds = false;
                return;
            }

            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;

            if (player == null || world == null) return;
            currentSpot = getSpot(player);
            if (currentSpot < 1) {
                count = 0;
                inBounds = false;
                return;
            } else {
                inBounds = true;
            }

            Box box = REGIONS[currentSpot - 1];
            count = getCount(world, player, box);
        });
    }

    private static int getCount(ClientWorld world, ClientPlayerEntity player, Box box) {
        List<Entity> entities = world.getOtherEntities(player, box);
        int currentCount = 0;
        for (Entity entity : entities) {
            if (EntityUtil.isARealPlayer(entity)) {
                currentCount++;
            }
        }

        return currentCount;
    }

    private static int getSpot(ClientPlayerEntity player) {
        Vec3d pos = player.getEntityPos();
        if (HEE2_BOX.contains(pos)) return 2;
        if (EE2_BOX.contains(pos)) return 2;
        else if (EE3_BOX.contains(pos)) return 3;
        else if (CORE_BOX.contains(pos)) return 4;
        else if (RELIC_BOX.contains(pos) && !Phase.inP5()) return 5;
        else return -1;
    }

    private static int getMaxCount() {
        if (currentSpot == 2 && Floor7.assumeSplitEE2) return 3;
        if (currentSpot == 3 && Floor7.assumeCore) return 3;
        return 4;
    }

    public static boolean display() {
        return Floor7.leapNotifications && inBounds && Phase.inBoss() && Location.inDungeon();
    }

    public static void render(HUDComponent component, DrawContext context) {
        int maxCount = getMaxCount();

        String startFormat;
        startFormat = (maxCount - count <= 1? "§9" : "§4");

        RenderUtils.drawCenteredText(context, component, Text.literal(startFormat + count + "§9/" + maxCount + " Players Leaped"));
    }
}
