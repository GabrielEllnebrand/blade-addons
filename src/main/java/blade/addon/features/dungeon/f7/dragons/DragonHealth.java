package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DragonHealth {

    static class DataHolder {
        EnderDragon dragon;
        float health;

        public DataHolder(EnderDragon dragon, float health) {
            this.dragon = dragon;
            this.health = health;
        }
    }

    static final ConcurrentLinkedQueue<DataHolder> dragons = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;

            if (entity instanceof EnderDragon dragon) {
                dragons.add(new DataHolder(dragon, dragon.getHealth()));
            }

            return false;
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> dragons.removeIf(dataHolder -> dataHolder.dragon.isRemoved()));
        RenderingEvents.FILLED_ENTITY.register(DragonHealth::render);
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            reset();
            return false;
        });
    }

    private static void reset() {
        dragons.clear();
    }

    private static int getColor(float health) {
        if (health >= 7.5e8) return Constants.GREEN;
        if (health >= 5e8) return Constants.YELLOW;
        if (health >= 2.5e8) return Constants.GOLD;
        return Constants.RED;
    }

    private static void render(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!Floor7.dragonHealth) return;

        dragons.forEach(dataHolder -> {

            EnderDragon dragon = dataHolder.dragon;
            float currHealth =  dragon.getHealth();

            if (currHealth != 1024.0f) {
                dataHolder.health = currHealth;
            }

            float health = dataHolder.health;

            if (health == 0) return;
            Vec3 pos = EntityUtil.getLerpedPos(dragon);
            RenderUtils.renderText(context, matrixStack, Component.literal(RenderUtils.formatNumber(health)).withColor(getColor(health)), pos, 5);

        });

    }

}
