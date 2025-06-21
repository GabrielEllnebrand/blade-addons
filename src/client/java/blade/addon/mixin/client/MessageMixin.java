package blade.addon.mixin.client;

import blade.addon.config.Config;
import blade.addon.utility.Constants;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MessageMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void sendMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!Config.configInstance.hideSkyblockerMessages) return;
        if (message.contains(Constants.SKYBLOCKER_TEXT)) ci.cancel();
    }
}
