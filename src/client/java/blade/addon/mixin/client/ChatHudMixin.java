package blade.addon.mixin.client;


import blade.addon.config.Config;
import blade.addon.utility.Constants;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at=@At("HEAD"), cancellable = true)
    public void addMessage(Text message, CallbackInfo ci) {
        if (!Config.configInstance.hideSkyblockerMessages) return;
        if (message.contains(Constants.SKYBLOCKER_TEXT)) ci.cancel();
    }

}
