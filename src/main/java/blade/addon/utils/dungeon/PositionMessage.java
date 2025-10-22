package blade.addon.utils.dungeon;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PositionMessage {

    private final String message;
    private final Vec3d min, max;
    private boolean visited;

    public PositionMessage(String message, Vec3d min, Vec3d max) {
        this.message = message;
        this.min = min;
        this.max = max;
        this.visited = false;
    }

    private boolean inRange(Vec3d pos) {
        return pos.x >= min.x && pos.x <= max.x
                && pos.y >= min.y && pos.y <= max.y
                && pos.z >= min.z && pos.z <= max.z;
    }

    public void tick(ClientPlayerEntity player) {
        if (visited) return;
        if (inRange(player.getPos())) {
            visited = true;
            player.networkHandler.sendChatCommand("pc " + message);
        }
    }

    public void reset() {
        visited = false;
    }
}
