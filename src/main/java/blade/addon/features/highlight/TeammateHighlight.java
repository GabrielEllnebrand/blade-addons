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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TeammateHighlight {

    private static final ConcurrentLinkedQueue<Player> teammates = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof Player player) {
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

        RenderingEvents.LINE_NO_DEPTH.register(TeammateHighlight::renderOutline);
    }

    private static void renderOutline(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!Dungeons.highlightTeammates && !Dungeons.renderClassName) return;

        teammates.forEach(player -> {
            if (Dungeons.dontHighlightHiddenTeammates && HidePlayers.shouldHidePlayers(player)) return;
            if (Dungeons.dontHighlightVisibleTeammates && !HidePlayers.shouldHidePlayers(player)) return;

            DungeonClass clazz = DungeonClass.getClass(player);
            if (clazz == null) return;

            int color = DungeonClass.getColor(clazz);

            if (Dungeons.highlightTeammates) {
                float[] rgba = RenderUtils.toFloats(color);
                RenderUtils.renderOutlinedBox(matrixStack, consumer,EntityUtil.getBox(player), rgba);
            }

            if (Dungeons.renderClassName) {
                Component text = Component.literal(player.getName().getString()).withColor(color).append(Component.literal(" [" + DungeonClass.getChar(clazz) + "]").withColor(Constants.YELLOW));
                Vec3 pos = EntityUtil.getLerpedPos(player);
                RenderUtils.renderText(context, matrixStack, text, pos.x(), pos.y() + 2.75, pos.z(), 2);
            }
        });
    }

}
