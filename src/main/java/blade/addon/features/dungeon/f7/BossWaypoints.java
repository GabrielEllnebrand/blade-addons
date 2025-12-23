package blade.addon.features.dungeon.f7;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Waypoint;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

public class BossWaypoints {

    private static final String filePath = FolderUtility.OLD_PATH + FolderUtility.WAYPOINTS_NAME;
    private static final String WAYPOINTS_NAME = "waypoints";

    public static final CopyOnWriteArrayList<Waypoint> waypoints = new CopyOnWriteArrayList<>();

    private static boolean ignoreBoss = false;
    private static boolean place = false;

    public static void init() {
        load();
        //needs separate consumers because of depth checking or smth
        WorldRenderEvents.BEFORE_ENTITIES.register(BossWaypoints::renderThroughWall);
        WorldRenderEvents.BEFORE_ENTITIES.register(BossWaypoints::render);
        UseBlockCallback.EVENT.register(BossWaypoints::onBlock);
    }

    public static boolean isInValidArea() {
        if (!Floor7.enableBossWaypoints) return false;
        if (ignoreBoss) return true;

        return Location.inDungeon() && Phase.inBoss();
    }

    public static boolean getPlace() {
        return place;
    }

    public static void setPlace(boolean shouldPlace) {
        place = shouldPlace;
        Misc.addChatMessage(Text.literal("Edit mode: ").append(Misc.getStatusText(place)));
    }

    public static void setIgnoreBoss(boolean shouldIgnoreBoss) {
        ignoreBoss = shouldIgnoreBoss;
        Misc.addChatMessage(Text.literal("Ignore boss: ").append(Misc.getStatusText(ignoreBoss)));
    }

    public static void togglePlace() {
        setPlace(!place);
    }

    public static void toggleIgnoreBoss() {
        setIgnoreBoss(!ignoreBoss);
    }

    public static void attemptAddWaypoint(double x, double y, double z, double dx, double dy, double dz) {
        if (waypointExists(x, y, z)) return;
        addWaypoint(x, y, z, dx, dy, dz);
    }

    private static void addWaypoint(double x, double y, double z, double dx, double dy, double dz) {
        float[] colorAsFloat = RenderUtils.toFloats(Floor7.nextWaypointColor);
        waypoints.add(new Waypoint(x, y, z, dx, dy, dz, colorAsFloat[0], colorAsFloat[1], colorAsFloat[2], colorAsFloat[3],Floor7.nextWaypointThroughWall));
        save();
    }

    public static Waypoint getWaypoint(double x, double y, double z) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.samePosition(x, y, z)) return waypoint;
        }
        return null;
    }

    public static void removeWaypoints(double x, double y, double z, double range) {
        for (int i = waypoints.size() - 1; i >= 0; i--) {
            if (waypoints.get(i).inRange(x, y, z, range)) {
                waypoints.remove(i);
            }
        }
        save();
    }

    public static void removeWaypoint(double x, double y, double z) {
        Waypoint waypoint = getWaypoint(x, y, z);
        if (waypoint == null) return;
        waypoints.remove(waypoint);
        save();
    }

    private static ActionResult onBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!isInValidArea() || !Misc.isClientPlayer(playerEntity)) return ActionResult.PASS;
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;

        BlockPos pos = blockHitResult.getBlockPos();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!place) return ActionResult.PASS;

        Waypoint waypoint = getWaypoint(x, y, z);
        if (waypoint == null) {
            addWaypoint(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
        } else {
            waypoints.remove(waypoint);
        }

        save();
        return ActionResult.PASS;
    }

    private static void renderThroughWall(WorldRenderContext worldRenderContext) {
        if (!isInValidArea()) return;

        Camera camera = worldRenderContext.camera();
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrixStack = worldRenderContext.matrixStack();
        if (matrixStack == null) return;
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumerProvider consumers = worldRenderContext.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER);

        waypoints.forEach(waypoint -> {
            if (waypoint.isThroughWall()) {
                waypoint.Render(consumer, matrixStack);
            }
        });
        matrixStack.pop();

    }

    private static void render(WorldRenderContext worldRenderContext) {
        if (!isInValidArea()) return;

        Camera camera = worldRenderContext.camera();
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrixStack = worldRenderContext.matrixStack();
        if (matrixStack == null) return;
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumerProvider consumers = worldRenderContext.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.THROUGH_WALL_FILLED_LAYER);

        waypoints.forEach(waypoint -> {
            if (!waypoint.isThroughWall()) {
                waypoint.Render(consumer, matrixStack);
            }
        });
        matrixStack.pop();

    }

    public static boolean waypointExists(double x, double y, double z) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.samePosition(x, y, z)) return true;
        }
        return false;
    }

    public static void save() {

        JsonObject obj = new JsonObject();
        Gson gson = new Gson();

        obj.add(WAYPOINTS_NAME, gson.toJsonTree(waypoints));

        JsonElement tree = gson.toJsonTree(obj);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(tree.toString());
        } catch (IOException ignored) {

        }
    }

    public static void load() {
        String jsonContent;
        try {
            jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ignored) {
            return;
        }
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(jsonContent, JsonObject.class);
        JsonElement element = object.get(WAYPOINTS_NAME);

        if (!element.isJsonArray()) return;
        JsonArray array = element.getAsJsonArray();

        for (JsonElement waypointElement : array) {
            JsonObject waypoint = waypointElement.getAsJsonObject();

            try {
                if (!waypoint.has("x") || !waypoint.has("y") || !waypoint.has("z")
                        || !waypoint.has("dx") || !waypoint.has("dy") || !waypoint.has("dz")
                        || !waypoint.has("r") || !waypoint.has("g") || !waypoint.has("b") || !waypoint.has("a")
                        || !waypoint.has("throughWall")) continue;

                double x = waypoint.get("x").getAsDouble();
                double y = waypoint.get("y").getAsDouble();
                double z = waypoint.get("z").getAsDouble();
                double dx = waypoint.get("dx").getAsDouble();
                double dy = waypoint.get("dy").getAsDouble();
                double dz = waypoint.get("dz").getAsDouble();

                float r = waypoint.get("r").getAsFloat();
                float g = waypoint.get("g").getAsFloat();
                float b = waypoint.get("b").getAsFloat();
                float a = waypoint.get("a").getAsFloat();

                boolean throughWall = waypoint.get("throughWall").getAsBoolean();
                waypoints.add(new Waypoint(x, y, z, dx, dy, dz, r, g, b, a, throughWall));
            } catch (NumberFormatException e) {
                System.out.println("msg: " + e.getMessage());
            }
        }
    }
}
