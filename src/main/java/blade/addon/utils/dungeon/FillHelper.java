package blade.addon.utils.dungeon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FillHelper {

    public static final String ENDER_PEARL = "Ender Pearl";
    public static final String SUPERBOOM_TNT = "Superboom TNT";
    public static final String INFLATABLE_JERRY = "Inflatable Jerry";

    private static final int FAIL = 0;
    private static final int SUCCESS = 1;

    /**
     * @param client to get access to the player and networkHandler
     * @param itemName Name of item
     * @param minThreshold an integer that the item count has to be lower than to fill
     * @param maxCount the max count of the item
     * @return 1 if it succeeds else 0 for fail
     */
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

        int itemsToGive = maxCount - currentCount;
        player.networkHandler.sendChatCommand("gfs " + itemName + " " + itemsToGive);
        return SUCCESS;
    }
}
