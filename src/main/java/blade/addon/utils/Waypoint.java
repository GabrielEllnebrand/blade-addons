package blade.addon.utils;

import blade.addon.utils.rendering.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;

public class Waypoint {
    private final double x, y, z, dx, dy, dz;
    private final float r, g, b, a;
    private final boolean throughWall;


    public Waypoint(double x, double y, double z, double dx, double dy, double dz, float r, float g, float b, float a, boolean throughWall) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.throughWall = throughWall;
    }

    public boolean samePosition(double x, double y, double z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean inRange(double x, double y, double z, double range) {
        double distanceSquared = (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
        return distanceSquared < range * range;
    }

    public boolean isThroughWall() {
        return throughWall;
    }

    public void Render(VertexConsumer consumer, PoseStack matrixStack) {
        RenderUtils.renderFilledBox(matrixStack, consumer, new AABB(x, y, z, x + dx, y + dy, z + dz), new float[]{r, g, b,a});
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", dx=" + dx +
                ", dy=" + dy +
                ", dz=" + dz +
                ", r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                ", throughWall=" + throughWall +
                '}';
    }
}
