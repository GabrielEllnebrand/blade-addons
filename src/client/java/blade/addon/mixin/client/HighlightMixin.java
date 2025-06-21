package blade.addon.mixin.client;

import blade.addon.config.Config;
import blade.addon.rendering.Render;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(EntityRenderDispatcher.class)
public class HighlightMixin {

    @Unique
    private static final double MAX_SQUARED_DISTANCE = 25;

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private <E extends Entity> void render(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity.getType() == EntityType.ARMOR_STAND) {
            renderHypixelHighlight(entity, x, y, z, matrices, vertexConsumers);
        } else {
            renderVanillaHighlight(entity, x, y, z, matrices, vertexConsumers);
        }
    }

    @Unique
    private <E extends Entity> void renderHypixelHighlight(E entity, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Text text = entity.getDisplayName();

        if (notInList(text, Config.configInstance.entityHypixelNames)) return;

        MinecraftClient client = MinecraftClient.getInstance();

        Entity realEntity = null;
        double smallestDistanceSquared = Integer.MAX_VALUE;

        //Hypixel has its entities recognised by armour stands
        //But we want to highlight the actual entity so we this finds the actual entity
        assert client.world != null;
        for (Entity comparisionEntity : client.world.getEntities()) {
            if (comparisionEntity.getType() == EntityType.ARMOR_STAND) continue;

            double distanceSquared = comparisionEntity.squaredDistanceTo(entity);

            if (distanceSquared < smallestDistanceSquared) {
                smallestDistanceSquared = distanceSquared;
                realEntity = comparisionEntity;
            }
        }

        //should most likely never happen
        if (smallestDistanceSquared > MAX_SQUARED_DISTANCE) return;

        VertexConsumer buffer = vertexConsumers.getBuffer(Render.getRenderLayer());

        EntityDimensions dimension = realEntity.getDimensions(realEntity.getPose());
        Box box = dimension.getBoxAt(x, y, z);
        double yOffset = entity.getY() - realEntity.getY();

        float[] rgba = new float[4];
        Config.configInstance.highlightColor.getComponents(rgba);
        VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY - yOffset, box.minZ, box.maxX, box.maxY - yOffset, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    @Unique
    private <E extends Entity> void renderVanillaHighlight(E entity, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Text text = entity.getDisplayName();

        if (notInList(text, Config.configInstance.entityVanillaNames)) return;

        VertexConsumer buffer = vertexConsumers.getBuffer(Render.getRenderLayer());

        EntityDimensions dimension = entity.getDimensions(entity.getPose());
        Box box = dimension.getBoxAt(x, y, z);

        float[] rgba = new float[4];
        Config.configInstance.highlightColor.getComponents(rgba);
        VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }


    @Unique
    private boolean notInList(Text text, ArrayList<String> list) {
        if (text == null) return true;

        String asString = text.toString();

        for (String str : list) {
            if (asString.contains(str)) return false;
        }

        return true;
    }

}
