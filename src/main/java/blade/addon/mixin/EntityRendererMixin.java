package blade.addon.mixin;

import blade.addon.utils.config.values.Dungeons;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<S extends EntityRenderState> {

    @Inject(method = "renderLabelIfPresent", at=@At("HEAD"), cancellable = true)
    private void hideNameTag(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!Dungeons.hideBlazeNameTag || text == null) return;
        String string = text.getString();
        if (string.contains("Blaze")) ci.cancel();

    }
}
