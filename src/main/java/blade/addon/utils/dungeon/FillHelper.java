package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FillHelper {

    public static final String ENDER_PEARL = "Ender Pearl";
    public static final String SUPERBOOM_TNT = "Superboom TNT";
    public static final String INFLATABLE_JERRY = "Inflatable Jerry";
    public static final String DECOY = "Decoy";

    /**
     * @param itemName Name of item
     * @param minThreshold an integer that the item count has to be lower than to fill
     * @param maxCount the max count of the item
     * @return 1 if it succeeds else 0 for fail
     */
    public static int fillItem(String itemName, int minThreshold, int maxCount, boolean needAtleastOne) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null) return Constants.FAIL;

        Inventory inventory = player.getInventory();

        int currentCount = 0;

        for (int i = 0; i < inventory.size(); i++) {

            ItemStack item = inventory.getStack(i);

            if (item.getName().toString().contains(itemName)) {

                int highestCount = item.getCount();

                if (highestCount > currentCount) {
                    currentCount = highestCount;

                    if (highestCount >= maxCount) return Constants.FAIL;
                }
            }

        }

        if ((needAtleastOne && currentCount == 0) || minThreshold < currentCount) return Constants.FAIL;

        int itemsToGive = maxCount - currentCount;
        player.networkHandler.sendChatCommand("gfs " + itemName + " " + itemsToGive);
        return Constants.SUCCESS;
    }

    public static int fillItem(MinecraftClient client, String itemName, int minThreshold, int maxCount) {
        ClientPlayerEntity player = client.player;

        if (player == null) return Constants.FAIL;

        Inventory inventory = player.getInventory();

        int currentCount = 0;

        for (int i = 0; i < inventory.size(); i++) {

            ItemStack item = inventory.getStack(i);

            if (item.getName().toString().contains(itemName)) {

                int highestCount = item.getCount();

                if (highestCount > currentCount) {
                    currentCount = highestCount;

                    if (highestCount >= maxCount) return Constants.FAIL;
                }
            }

        }

        if ( minThreshold < currentCount) return Constants.FAIL;

        int itemsToGive = maxCount - currentCount;
        player.networkHandler.sendChatCommand("gfs " + itemName + " " + itemsToGive);
        return Constants.SUCCESS;
    }
}
