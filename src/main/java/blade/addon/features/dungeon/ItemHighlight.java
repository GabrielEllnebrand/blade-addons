package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.List;

public class ItemHighlight {

    private static final HashSet<String> ITEMS = new HashSet<>(List.of("Revive Stone", "Trap", "Decoy", "Inflatable Jerry", "Defuse Kit", "Dungeon Chest Key", "Treasure Talisman", "Architect's First Draft", "Spirit Leap", "Healing VIII Splash Potion", "Training Weights", "Candycomb"));

    @ConfigValue
    public static boolean highlightItems = false;

    public static void init() {
    }

    public static boolean highlightItem(ItemEntity item) {
        if (!highlightItems || !Location.inDungeon() || Phase.inBoss()) return false;

        Text itemText = item.getStack().getName();
        if (itemText == null) return false;
        String itemName = itemText.getString();
        System.out.println(item);

        return ITEMS.contains(itemName);
    }

    public static int getColor(ItemEntity item) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;
        double distance = item.getPos().distanceTo(player.getPos());
        if (distance > 20) {
            return 0;
        } else if (distance > 3.5) {
            return Constants.RED_COLOR;
        } else if (item.age > 11){
            return Constants.GREEN_COLOR;
        } else {
            return Constants.ORANGE_COLOR;
        }
    }
}
