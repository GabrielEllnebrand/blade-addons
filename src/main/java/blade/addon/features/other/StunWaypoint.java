package blade.addon.features.other;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class StunWaypoint {

    private static final Box BLOCK = new Box(-168, 27, -169, -167, 28, -168);

    private static boolean inBelly = false;
    private static boolean inP3 = false;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.in(Location.KUUDRA) || !ExtraOptions.stunWaypoint) return false;
            String string = text.getString();

            if (string.equals("[NPC] Elle: Phew! The Ballista is finally ready! It should be strong enough to tank Kuudra's blows now!")) {
                inP3 = true;
                return false;
            }

            if (string.equals("[NPC] Elle: POW! SURELY THAT'S IT! I don't think he has any more in him!")) {
                reset();
                return false;
            }

            return false;
        });

        Events.ON_PACKET.register(packet -> {
            if (!Location.in(Location.KUUDRA) || !ExtraOptions.stunWaypoint) return false;

            if (packet instanceof PlayerPositionLookS2CPacket posPacket) {
                Vec3d pos = posPacket.change().position();
                if (pos.x == -161 && pos.y == 49 && pos.z == -186) {
                    inBelly = true;
                }

            }
            return false;
        });

        RenderingEvents.NO_DEPTH_OUTLINE_ENTITY.register((context, matrixStack, consumer) -> {
            if (!inP3 || !inBelly || !Location.in(Location.KUUDRA) || !ExtraOptions.stunWaypoint) return;
            RenderUtils.renderOutline(matrixStack, consumer, BLOCK, new float[]{0, 1, 1, 1});
        });
    }


    private static void reset() {
        inBelly = false;
        inP3 = false;
    }
}
