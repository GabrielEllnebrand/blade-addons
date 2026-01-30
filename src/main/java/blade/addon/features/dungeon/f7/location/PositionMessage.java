package blade.addon.features.dungeon.f7.location;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Section;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PositionMessage {

    private final String message;
    private final String[] checks;
    private final Box box;
    private boolean sent;
    private final int[] sections;

    public PositionMessage(String message, String[] checks, Box box, int[] sections) {
        this.message = message;
        this.checks = checks;
        this.box = box;
        this.sent = true;
        this.sections = sections;
        PositionMessages.positionMessages.add(this);
    }

    private boolean inRange(Vec3d pos) {
        return box.contains(pos);
    }

    private boolean inAValidSection() {
        for (int section : sections) {
            if (Section.inSection(section)) return true;
        }
        return false;
    }

    public void tick(ClientPlayerEntity player) {
        if (sent || !inAValidSection() || !inRange(player.getEntityPos())) return;
        sent = true;
        if (Floor7.enablePositionalMessages) {
            Misc.executeCommand("pc " + message);
        }
    }

    private boolean matchesACheck(String string) {
        for (String check : checks) {
            if (string.contains(check)) return true;
        }
        return false;
    }

    public boolean hasBeenSent(String string) {
        if (!matchesACheck(string.toLowerCase())) return false;

        if (sent) {
            return true;
        } else {
            sent = true;
            return false;
        }
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean sent() {
        return sent;
    }

    public Box getBox() {
        return box;
    }
}
