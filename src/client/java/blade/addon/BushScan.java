package blade.addon;

import blade.addon.config.Config;
import blade.addon.utility.JsonUtility;
import blade.addon.utility.Location;
import blade.addon.utility.Locations;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BushScan {
    private static final double REFRESH_TICK = 40;

    private static final ArrayList<BlockPos> positions = JsonUtility.loadBlockPosList("/data/bush-positions.json");

    private static volatile ArrayList<BlockPos> flowering = new ArrayList<>();
    private static volatile ArrayList<BlockPos> growing = new ArrayList<>();

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

        ArrayList<BlockPos> floweringTemp = new ArrayList<>();
        ArrayList<BlockPos> growingTemp = new ArrayList<>();

        Position origin = player.getPos();

        double maxDistanceSquared = Config.configInstance.bushDistance * Config.configInstance.bushDistance;

        for (BlockPos pos : positions) {
            double distanceSquared = pos.getSquaredDistance(origin);

            if (distanceSquared < maxDistanceSquared) {

                if (world.getBlockState(pos).getBlock() == Blocks.FLOWERING_AZALEA) {
                    floweringTemp.add(pos);
                } else {
                    growingTemp.add(pos);
                }

            }

        }

        flowering = floweringTemp;
        growing = growingTemp;

    }

    public static ArrayList<BlockPos> getFlowering() {
        return flowering;
    }

    public static ArrayList<BlockPos> getGrowing() {
        return growing;
    }

}
