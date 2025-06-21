package blade.addon;

import blade.addon.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FillHelper {

    private static final int MIN_COUNT_AUTO_PEARL = 4;
    private static final int MIN_COUNT_AUTO_SUPERBOOM = 32;

    private static final int FAIL = 0;
    private static final int SUCCESS = 1;

    private static final double REFRESH_TICK = 80;

    public static final String ENDER_PEARL = "Ender Pearl";
    public static final String SUPERBOOM_TNT = "Superboom TNT";

    private static double totalTicks = 0;

    public static void tick(MinecraftClient client) {

        totalTicks++;
        if (totalTicks < REFRESH_TICK) return;
        totalTicks = 0;

        if (fillPearl(client) == SUCCESS) return;

        fillSuperBoom(client);
    }

    private static int fillPearl(MinecraftClient client) {
        if (!Config.configInstance.autoFillPearl) return FAIL;
        return fillItem(client, ENDER_PEARL, MIN_COUNT_AUTO_PEARL, 16);
    }

    private static void fillSuperBoom(MinecraftClient client) {
        if (!Config.configInstance.autoFillSuperBoom) return;
        fillItem(client, SUPERBOOM_TNT, MIN_COUNT_AUTO_SUPERBOOM, 64);
    }


    public static int fillItem(MinecraftClient client, String itemName, int minThreshold, int maxCount) {
        ClientPlayerEntity player = client.player;

        if (player == null) return FAIL;

        Inventory inventory = player.getInventory();

        int currentCount = 0;

        for (int i = 0; i < inventory.size(); i++) {

            ItemStack item = inventory.getStack(i);

            if (item.getName().toString().contains(itemName)) {

                int highestCount = item.getCount();

                if (highestCount > currentCount) {
                    currentCount = highestCount;

                    if (highestCount >= maxCount) return FAIL;
                }
            }

        }

        if (minThreshold < currentCount) return FAIL;

        int pearlsToGive = maxCount - currentCount;
        player.networkHandler.sendChatCommand("gfs " + itemName + " " + pearlsToGive);
        return SUCCESS;
    }
}
