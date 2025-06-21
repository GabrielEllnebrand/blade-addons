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
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BugScan {
    private static final double REFRESH_TICK = 40;
    private static final double MAX_GUESS_DISTANCE_SQUARED = 9;

    private static final ArrayList<BlockPos> positions = JsonUtility.loadBlockPosList("/data/bug-positions.json");

    private static volatile ArrayList<BlockPos> possibleBugs = new ArrayList<>();

    private static double totalTicks = 0;

    public static void tick(@NotNull MinecraftClient client) {
        totalTicks++;
        if (totalTicks < REFRESH_TICK) return;
        totalTicks = 0;

        if (!Config.configInstance.enableBush) return;

        World world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null) return;

        if (!Location.in(Locations.GALATEA)) return;

        ArrayList<BlockPos> possibleBugsTemp = new ArrayList<>();

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof ArmorStandEntity armorStand) {
                BlockPos pos = armorStand.getBlockPos();

                if (inGuessDistance(pos)) {
                    possibleBugsTemp.add(pos);
                }

            }
        }

        possibleBugs = possibleBugsTemp;
    }

    public static boolean inGuessDistance(BlockPos pos) {
        for (BlockPos bugPos : positions) {

            if (pos.getSquaredDistance(bugPos) <= MAX_GUESS_DISTANCE_SQUARED) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<BlockPos> getPossibleBugs() {
        return possibleBugs;
    }
}
