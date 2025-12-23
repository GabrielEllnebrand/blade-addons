package blade.addon.features.dungeon.f7;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.times.PersonalBests;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class PredevTimer {

    private static long bossEnterTime = 0;
    private static boolean at3rdDev = false;
    private static boolean enteredBoss = false;

    public static void init() {

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inP1()) {
                bossEnterTime = System.currentTimeMillis();
                at3rdDev = false;
                enteredBoss = true;
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> enteredBoss = false);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!enteredBoss || at3rdDev || (!Floor7.predevForAll && !DungeonClass.isClass(DungeonClass.HEALER))) return;
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (Misc.getDistance(player.getX(), player.getZ(), 1, 77) <= 3) {
                at3rdDev = true;
            }
        });

        Events.ON_LEAP.register(message -> {
            if (at3rdDev && enteredBoss && (DungeonClass.isClass(DungeonClass.HEALER) || Floor7.predevForAll)) {
                PersonalBests.predevTime.testNewTime(Text.literal("§aPredev completed in "), bossEnterTime);
                at3rdDev = false;
                enteredBoss = false;
            }
        });

    }


}
