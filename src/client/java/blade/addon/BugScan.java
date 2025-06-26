package blade.addon;

import blade.addon.config.Config;
import blade.addon.utility.JsonUtility;
import blade.addon.utility.Location;
import blade.addon.utility.Locations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BugScan {
    private static final double REFRESH_TICK = 40;

    private static final ArrayList<BlockPos> positions = JsonUtility.loadBlockPosList("/data/bug-positions.json");

    private static volatile ArrayList<Box> possibleBugs = new ArrayList<>();

    private static double totalTicks = 0;

    public static void tick(@NotNull MinecraftClient client) {
        totalTicks++;
        if (totalTicks < REFRESH_TICK) return;
        totalTicks = 0;

        if (!Config.configInstance.enableBug) return;

        World world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null) return;

        if (!Location.in(Locations.GALATEA)) return;

        ArrayList<Box> possibleBugsTemp = new ArrayList<>();

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof ArmorStandEntity armorStand) {
                BlockPos pos = armorStand.getBlockPos();

                if (inGuessDistance(pos)) {
                    possibleBugsTemp.add(armorStand.getBoundingBox());
                }

            }
        }

        possibleBugs = possibleBugsTemp;
    }

    public static boolean inGuessDistance(BlockPos pos) {
        double maxDistanceSquared = Config.configInstance.bugDistance * Config.configInstance.bugDistance;
        for (BlockPos bugPos : positions) {
            if (pos.getSquaredDistance(bugPos) <= maxDistanceSquared) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Box> getPossibleBugs() {
        return possibleBugs;
    }
}
