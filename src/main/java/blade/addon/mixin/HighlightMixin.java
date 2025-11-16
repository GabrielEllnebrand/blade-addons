package blade.addon.mixin;

import blade.addon.features.dungeon.MobHighlight;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class HighlightMixin {

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private <E extends Entity> void render(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!MobHighlight.hasEntity(entity)) return;

        //hides shadow assassins
        if (entity.isInvisible() && MobHighlight.dontShowInvisibleMobs && entity instanceof PlayerEntity) return;

        EntityDimensions dimension = entity.getDimensions(entity.getPose());
        Box box = dimension.getBoxAt(x, y, z);

        if (entity instanceof WitherEntity) {
            box = box.expand(1, 0, 1);
        }

        //only shows the head
        if (entity instanceof EndermanEntity && entity.isInvisible() && MobHighlight.dontShowInvisibleMobs) {
            box = box.expand(0, -1.8, 0).offset(0, -1.2, 0);
        }

        //bigger mimic highlight
        if (entity instanceof ZombieEntity zombie) {
            if (zombie.isBaby()) {
                box = box.expand(0.15, 0.2, 0.15);
            }
        }


        if (MobHighlight.renderFilled()) {
            int filledColor = MobHighlight.getFilledColor(entity);
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);
            float[] rgba = RenderUtils.toFloats(filledColor);
            VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
        }
        if (MobHighlight.renderOutline()) {
            int outlineColor = MobHighlight.getOutlineColor(entity);
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayers.OUTLINE_ENTITY_LAYER);
            VertexRendering.drawOutline(matrices, buffer, VoxelShapes.cuboid(box), 0, 0, 0, outlineColor);
        }
    }

}
