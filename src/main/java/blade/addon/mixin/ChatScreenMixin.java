package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private static void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtraOptions.copyChat || click.button() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ChatHudInvoker hudInvoker = (ChatHudInvoker) mc.inGameHud.getChatHud();
        if (hudInvoker == null) return;

        double x = hudInvoker.lineX(click.x());
        double y = hudInvoker.lineY(click.y());

        String string;
        if (ExtraOptions.copyLineOnly) {
            int index = hudInvoker.getLineIndex(x, y);
            List<ChatHudLine.Visible> visibleMessages = hudInvoker.getVisibleMessages();
            if (visibleMessages == null || index < 0 || index >= visibleMessages.size()) return;

            ChatHudLine.Visible msg = visibleMessages.get(index);
            string = orderedTextToString(msg.content());
        } else {
            string = Misc.copyChat(hudInvoker, x, y);
        }

        if (string == null) return;

        if (ExtraOptions.removeColorCodes) {
            string = string.replaceAll("§.", "");
        }

        if (ExtraOptions.replaceColorChars) {
            string = string.replaceAll("§", "&");
        }

        mc.keyboard.setClipboard(string);
    }

    @Unique
    private static String orderedTextToString(OrderedText text) {
        StringBuilder builder = new StringBuilder();

        text.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });

        return builder.toString();
    }
}
