package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

   @ModifyVariable(method = "drawCooldownProgress", at=@At("STORE"), ordinal = 0)
    private float noCooldown(float f) {
        return Visual.hideCooldown? 0: f;
    }

}
