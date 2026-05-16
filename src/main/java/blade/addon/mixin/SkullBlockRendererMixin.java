package blade.addon.mixin;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Visual;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin {

    @Unique
    private static final Identifier ESSENCE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "/textures/entity/essence.png");

    @Shadow
    public static RenderType getSkullRenderType(SkullBlock.Type type, @Nullable Identifier texture) {
        return null;
    }

    @Inject(method = "resolveSkullRenderType", at = @At("HEAD"), cancellable = true)
    private void renderEssence(SkullBlock.Type skullType, SkullBlockEntity blockEntity, CallbackInfoReturnable<RenderType> cir) {
        if (!Visual.fixWitherEssence || skullType != SkullBlock.Types.PLAYER) return;

        ResolvableProfile profileComponent = blockEntity.getOwnerProfile();
        if (profileComponent == null) return;

        GameProfile profile = profileComponent.partialProfile();

        if (profile.id().toString().equals("e0f3e929-869e-3dca-9504-54c666ee6f23")) {
            cir.setReturnValue(getSkullRenderType(SkullBlock.Types.PLAYER, ESSENCE_TEXTURE));
        }
    }
}
