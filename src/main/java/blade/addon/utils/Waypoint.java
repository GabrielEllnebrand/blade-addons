package blade.addon.utils;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;

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

    public void Render(VertexConsumer consumer, MatrixStack matrixStack) {
        VertexRendering.drawFilledBox(matrixStack, consumer, x, y, z, x + dx, y + dy, z + dz, r, g, b, a);
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
