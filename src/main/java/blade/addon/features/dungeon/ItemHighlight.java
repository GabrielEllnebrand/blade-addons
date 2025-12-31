package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemHighlight {

    private static final HashSet<String> ITEMS = new HashSet<>(List.of("Revive Stone", "Trap", "Decoy", "Inflatable Jerry", "Defuse Kit", "Dungeon Chest Key", "Treasure Talisman", "Architect's First Draft", "Spirit Leap", "Healing VIII Splash Potion", "Training Weights", "Candycomb"));

    private static final ConcurrentHashMap<ItemEntity, Integer> trackedItems = new ConcurrentHashMap<>();


    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            ClientWorld world = minecraftClient.world;
            if (world == null) return;

            world.getEntities().forEach(entity -> {
                if (entity instanceof ItemEntity item) {
                    if (trackedItems.containsKey(item)) return;

                    if (highlightItem(item)) {
                        trackedItems.put(item, 0);
                    }
                }
            });
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity == null) return;
            if (entity instanceof ItemEntity item) {
                trackedItems.remove(item);
            }
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!Dungeons.highlightItems) return;
            Vec3d camera = context.worldState().cameraRenderState.pos;
            MatrixStack matrices = context.matrices();
            if (matrices == null) return;
            matrices.push();
            matrices.translate(-camera.x, -camera.y, -camera.z);

            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            VertexConsumer buffer = consumers.getBuffer(RenderLayers.THROUGH_WALL_FILLED_LAYER);

            double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

            trackedItems.forEach((itemEntity, integer) -> {
                double x =  MathHelper.lerp(tickProgress, itemEntity.lastRenderX, itemEntity.getX());
                double y =  MathHelper.lerp(tickProgress, itemEntity.lastRenderY, itemEntity.getY());
                double z =  MathHelper.lerp(tickProgress, itemEntity.lastRenderZ, itemEntity.getZ());

                EntityDimensions dimension = itemEntity.getDimensions(itemEntity.getPose());
                Box box = dimension.getBoxAt(x, y, z).expand(0.1).offset(0, 0.05, 0);
                float[] color = RenderUtils.toFloats(getColor(itemEntity));
                VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color[0], color[1], color[2], color[3]);
            });

            matrices.pop();
        });
    }

    public static boolean highlightItem(ItemEntity item) {
        if (!Dungeons.highlightItems || !Location.inDungeon() || Phase.inBoss()) return false;

        Text itemText = item.getStack().getName();
        if (itemText == null) return false;
        String itemName = itemText.getString();

        return ITEMS.contains(itemName);
    }

    public static int getColor(ItemEntity item) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;
        double distance = item.getEntityPos().distanceTo(player.getEntityPos());
        if (distance > 20) {
            return 0;
        } else if (distance > 3.5) {
            return Constants.RED_COLOR;
        } else if (item.age > 11){
            return Constants.GREEN_COLOR;
        } else {
            return Constants.ORANGE_COLOR;
        }
    }

    public static boolean hideItem(ItemEntity item) {
        if (item == null) return false;
        return trackedItems.containsKey(item);
    }
}
