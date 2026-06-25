package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemHighlight {

    private static final HashSet<String> ITEMS = new HashSet<>(List.of("Revive Stone", "Trap", "Decoy", "Inflatable Jerry", "Defuse Kit", "Dungeon Chest Key", "Treasure Talisman", "Architect's First Draft", "Spirit Leap", "Healing VIII Splash Potion", "Training Weights", "Candycomb"));

    private static final ConcurrentHashMap<ItemEntity, Integer> trackedItems = new ConcurrentHashMap<>();


    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            ClientLevel world = minecraftClient.level;
            if (world == null) return;

            world.entitiesForRendering().forEach(entity -> {
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

        RenderingEvents.FILLED_NO_DEPTH.register(ItemHighlight::render);
    }

    private static void render(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!Dungeons.highlightItems) return;
        double tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);


        trackedItems.forEach((itemEntity, integer) -> {
            double x =  Mth.lerp(tickProgress, itemEntity.xOld, itemEntity.getX());
            double y =  Mth.lerp(tickProgress, itemEntity.yOld, itemEntity.getY());
            double z =  Mth.lerp(tickProgress, itemEntity.zOld, itemEntity.getZ());

            EntityDimensions dimension = itemEntity.getDimensions(itemEntity.getPose());
            AABB box = dimension.makeBoundingBox(x, y, z).inflate(0.1).move(0, 0.05, 0);
            float[] color = RenderUtils.toFloats(getColor(itemEntity));
            RenderUtils.renderFilledBox(matrixStack, consumer, box, color);
        });
    }

    public static boolean highlightItem(ItemEntity item) {
        if (!Dungeons.highlightItems || !Location.inDungeon() || Phase.inBoss()) return false;

        Component itemText = item.getItem().getHoverName();
        if (itemText == null) return false;
        String itemName = itemText.getString();

        return ITEMS.contains(itemName);
    }

    public static int getColor(ItemEntity item) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;
        double distance = item.position().distanceTo(player.position());
        if (distance > 20) {
            return 0;
        } else if (distance > 3.5) {
            return Constants.RED;
        } else if (item.tickCount > 11){
            return Constants.GREEN;
        } else {
            return Constants.GOLD;
        }
    }

    public static boolean hideItem(ItemEntity item) {
        if (item == null) return false;
        return trackedItems.containsKey(item);
    }
}
