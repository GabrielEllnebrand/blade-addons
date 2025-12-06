package blade.addon.features.dungeon.f7;

import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.PositionMessage;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;

public class PositionMessages {

    private static final ArrayList<PositionMessage> positionMessages = JsonUtility.readPositionalMessages("/data/positions.json");

    public static void init() {
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                for (PositionMessage message : positionMessages) {
                    message.reset();
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(PositionMessages::tick);
    }

    public static void tick(MinecraftClient client) {
        if (!Location.inDungeon() || !Phase.inP3() || !Floor7.enablePositionalMessages) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        for (PositionMessage message : positionMessages) {
            message.tick(player);
        }
    }
}
