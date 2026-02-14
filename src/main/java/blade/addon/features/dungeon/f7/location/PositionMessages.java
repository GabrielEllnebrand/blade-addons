package blade.addon.features.dungeon.f7.location;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderingEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.util.ArrayList;

public class PositionMessages {

    static final ArrayList<PositionMessage> positionMessages = new ArrayList<>();

    static PositionMessage SS = new PositionMessage("At SS!", new String[]{"ss"}, new Box(107, 120, 93, 110, 121, 95), new int[]{0, 1});
    static PositionMessage EE2 = new PositionMessage("At EE2!", new String[]{"ee2", "early enter 2"}, new Box(57, 109, 130, 59, 110, 132), new int[]{1, 2});
    static PositionMessage HEE2 = new PositionMessage("At High EE2!", new String[]{"high ee2", "highee2", "hee2"}, new Box(59, 132, 138, 62, 133, 140), new int[]{1, 2});
    static PositionMessage EE3 = new PositionMessage("At EE3!", new String[]{"ee3", "early enter 3", "early entry 3"}, new Box(1, 109, 103, 3, 110, 106), new int[]{2, 3});
    static PositionMessage CORE = new PositionMessage("At Core!", new String[]{"core"}, new Box(53, 115, 51, 56, 116, 54), new int[]{2, 3, 4});
    static PositionMessage TUNNEL = new PositionMessage("Inside Goldor Tunnel!", new String[]{"tunnel"}, new Box(52, 114, 55, 57, 116, 58), new int[]{4, 5});
    static PositionMessage SAFE_2 = new PositionMessage("At 2 Safespot!", new String[]{"2 safespot", "ee2 safespot", "safespot ee2", "s2 safespot", "safespot s2"}, new Box(46, 109, 121.987, 49, 110, 121.988), new int[]{1, 2});
    static PositionMessage SAFE_3 = new PositionMessage("At 3 Safespot!", new String[]{"3 safespot", "ee3 safespot", "freaky ee3", "freak ee", "safespot s3", "safespot 3"}, new Box(18, 121, 91, 19, 126, 99), new int[]{2, 3});
    static PositionMessage SPLIT_2 = new PositionMessage("At Split ee2!", new String[]{"split ee2", "splitee2", "mage term"}, new Box(58, 119, 124, 60, 123, 126), new int[]{1, 2});
    static PositionMessage SAFE_2_HIGH = new PositionMessage("At High 2 Safespot!", new String[]{"2 safespot"}, new Box(70, 127, 143.7, 57, 133, 146.7), new int[]{1, 2});

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(PositionMessages::tick);
        RenderingEvents.OUTLINE_ENTITY.register(PositionMessages::render);
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            disableAll();
            return false;
        });

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inP2()) {
                SS.setSent(false);
            } else if (Phase.inTerminals()) {
                EE2.setSent(false);
                HEE2.setSent(false);
                EE3.setSent(false);
                CORE.setSent(false);
                TUNNEL.setSent(false);
                SAFE_2.setSent(false);
                SAFE_3.setSent(false);
                SPLIT_2.setSent(false);
                SAFE_2_HIGH.setSent(false);
            } else if (Phase.inGoldorTunnel()) {
                EE2.setSent(true);
                HEE2.setSent(true);
                EE3.setSent(true);
                CORE.setSent(true);
                TUNNEL.setSent(true);
                SAFE_2.setSent(true);
                SAFE_3.setSent(true);
                SPLIT_2.setSent(true);
                SAFE_2_HIGH.setSent(true);
            }
            return false;
        });


    }

    public static void tick(MinecraftClient client) {
        if (!Location.inDungeon() || !Floor7.enablePositionalMessages) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        for (PositionMessage message : positionMessages) {
            message.tick(player);
        }
    }

    public static boolean hasBeenSent(String message) {
        String lowered = message.toLowerCase();
        for (PositionMessage positionMessage : positionMessages) {
            if (positionMessage.hasBeenSent(lowered)) {
                return true;
            }
        }
        return false;
    }

    public static void disableAll() {
        for (PositionMessage positionMessage : positionMessages) {
            positionMessage.setSent(true);
        }
    }

    private static void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!Location.inDungeon() || !Debug.renderPositions) return;

        MatrixStack.Entry entry = matrixStack.peek();
        for (PositionMessage positionMessage : positionMessages) {
            Box box = positionMessage.getBox();
            if (positionMessage.sent()) {
                VertexRendering.drawBox(entry, consumer, box, 0, 1, 0, 1);
            } else {
                VertexRendering.drawBox(entry, consumer, box, 1, 0, 0, 1);
            }
        }
    }
}
