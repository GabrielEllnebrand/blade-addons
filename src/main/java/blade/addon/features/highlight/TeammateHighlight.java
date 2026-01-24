package blade.addon.features.highlight;

import blade.addon.features.dungeon.HidePlayers;
import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TeammateHighlight {

    private static final ConcurrentLinkedQueue<PlayerEntity> teammates = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof PlayerEntity player) {
                if (EntityUtil.isARealPlayer(player) && !EntityUtil.isClientPlayer(player) && !teammates.contains(player)) {
                    teammates.add(player);
                }
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            teammates.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> teammates.removeIf(Entity::isRemoved));

        RenderingEvents.NO_DEPTH_OUTLINE_ENTITY.register(TeammateHighlight::renderOutline);
    }

    private static void renderOutline(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!Dungeons.highlightTeammates && !Dungeons.renderClassName) return;

        teammates.forEach(player -> {
            if (Dungeons.dontHighlightHiddenTeammates && HidePlayers.shouldHidePlayers(player)) return;
            DungeonClass clazz = DungeonClass.getClass(player);
            if (clazz == null) return;

            int color = DungeonClass.getColor(clazz);

            if (Dungeons.highlightTeammates) {
                float[] rgba = RenderUtils.toFloats(color);
                RenderUtils.renderOutline(matrixStack, consumer, EntityUtil.getBox(player), rgba);
            }

            if (Dungeons.renderClassName) {
                Text text = Text.literal(player.getName().getString()).withColor(color).append(Text.literal(" [" + DungeonClass.getChar(clazz) + "]").withColor(Constants.YELLOW_COLOR));
                Vec3d pos = EntityUtil.getLerpedPos(player);
                RenderUtils.renderText(context, matrixStack, text, pos.getX(), pos.getY() + 2.75, pos.getZ(), 2);
            }
        });
    }

}
