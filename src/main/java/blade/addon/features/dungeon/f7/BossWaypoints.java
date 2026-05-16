package blade.addon.features.dungeon.f7;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Waypoint;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
        RenderingEvents.FILLED_BLOCK.register(BossWaypoints::render);
        RenderingEvents.NO_DEPTH_FILLED.register(BossWaypoints::renderThroughWall);
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

    public static boolean getIgnoreBoss() {
        return ignoreBoss;
    }

    public static void setPlace(boolean shouldPlace) {
        place = shouldPlace;
        Misc.addChatMessage(Component.literal("Edit mode: ").append(Misc.getStatusText(place)));
    }

    public static void setIgnoreBoss(boolean shouldIgnoreBoss) {
        ignoreBoss = shouldIgnoreBoss;
        Misc.addChatMessage(Component.literal("Ignore boss: ").append(Misc.getStatusText(ignoreBoss)));
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
        waypoints.add(new Waypoint(x, y, z, dx, dy, dz, colorAsFloat[0], colorAsFloat[1], colorAsFloat[2], colorAsFloat[3], Floor7.nextWaypointThroughWall));
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

    private static InteractionResult onBlock(Player playerEntity, Level world, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!isInValidArea() || !EntityUtil.isClientPlayer(playerEntity)) return InteractionResult.PASS;
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;

        BlockPos pos = blockHitResult.getBlockPos();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!place) return InteractionResult.PASS;

        Waypoint waypoint = getWaypoint(x, y, z);
        if (waypoint == null) {
            addWaypoint(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
        } else {
            waypoints.remove(waypoint);
        }

        save();
        return InteractionResult.PASS;
    }

    private static void render(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!isInValidArea()) return;

        waypoints.forEach(waypoint -> {
            if (waypoint.isThroughWall()) {
                waypoint.Render(consumer, matrixStack);
            }
        });
    }

    private static void renderThroughWall(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!isInValidArea()) return;

        waypoints.forEach(waypoint -> {
            if (!waypoint.isThroughWall()) {
                waypoint.Render(consumer, matrixStack);
            }
        });
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
