package blade.addon.features.dungeon;

import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.PositionMessage;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;

public class PositionMessages {

    @ConfigValue
    public static boolean enablePositionalMessages = false;

    private static final ArrayList<PositionMessage> positionMessages = JsonUtility.readPositionalMessages("/data/positions.json");

    public static void tick(MinecraftClient client) {
        if (!Location.inDungeon()) return;
        if (!Phase.inP3()) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        for (PositionMessage message: positionMessages) {
            message.tick(player);
        }
    }

    public static void reset() {
        for (PositionMessage message: positionMessages) {
            message.reset();
        }
    }
}
