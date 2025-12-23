package blade.addon.mixin;

import blade.addon.features.highlight.MobHighlight;
import blade.addon.utils.Location;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderMixin {

    @Unique
    private static final Box box = new Box(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375);

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("TAIL"))
    private static<E extends BlockEntity> void render(E blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (blockEntity instanceof TrappedChestBlockEntity) {
            if (!Location.inDungeon() || !MobHighlight.mobHighlight || !MobHighlight.highlightMimicChests) return;
            if (MobHighlight.renderFilled()) {
                VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);
                float[] rgba = RenderUtils.toFloats(MobHighlight.mimicFilledColor);
                VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
            }
            if (MobHighlight.renderOutline()) {
                VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayers.OUTLINE_ENTITY_LAYER);
                float[] rgba = RenderUtils.toFloats(MobHighlight.mimicOutlineColor);
                VertexRendering.drawBox(matrices, buffer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
            }
        }
    }
}
