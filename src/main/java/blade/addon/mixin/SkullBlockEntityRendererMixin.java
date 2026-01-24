package blade.addon.mixin;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Visual;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class SkullBlockEntityRendererMixin {

    @Unique
    private static Identifier ESSENCE_TEXTURE = Identifier.of(Constants.NAMESPACE, "/textures/entity/essence.png");

    @Shadow
    public static RenderLayer getCutoutRenderLayer(SkullBlock.SkullType type, @Nullable Identifier texture) {
        return null;
    }

    @Inject(method = "renderSkull", at = @At("HEAD"), cancellable = true)
    private void renderEssence(SkullBlock.SkullType skullType, SkullBlockEntity blockEntity, CallbackInfoReturnable<RenderLayer> cir) {
        if (!Visual.fixWitherEssence || skullType != SkullBlock.Type.PLAYER) return;

        ProfileComponent profileComponent = blockEntity.getOwner();
        if (profileComponent == null) return;

        GameProfile profile = profileComponent.getGameProfile();

        if (profile.id().toString().equals("e0f3e929-869e-3dca-9504-54c666ee6f23")) {
            cir.setReturnValue(getCutoutRenderLayer(SkullBlock.Type.PLAYER, ESSENCE_TEXTURE));
        }
    }
}
