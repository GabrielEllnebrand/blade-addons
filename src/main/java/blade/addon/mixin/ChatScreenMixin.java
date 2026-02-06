package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
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
            string = TextUtil.orderedTextToString(msg.content());
        } else {
            string = copyChat(hudInvoker, x, y);
        }

        if (string == null) return;

        if (ExtraOptions.removeColorCodes) {
            string = string.replaceAll("§.", "");
        }

        if (ExtraOptions.replaceColorChars) {
            string = string.replaceAll("§", "&");
        }

        mc.keyboard.setClipboard(string);

        if (ExtraOptions.copyChatFeedback) {
            Misc.addChatMessage(Text.literal("Copied chat message"));
        }
    }

    @Unique
    private static String copyChat(ChatHudInvoker hudInvoker, double x, double y) {
        List<ChatHudLine.Visible> messages = hudInvoker.getVisibleMessages();
        int endIndex = hudInvoker.getLineIndex(x, y);

        if (messages == null || endIndex < 0 || endIndex >= messages.size()) return null;

        int startIndex = endIndex;

        //find start of msg
        for (int i = endIndex; i >= 0; i--) {
            ChatHudLine.Visible chatHudLine = messages.get(i);
            if (chatHudLine.endOfEntry()) {
                startIndex = i;
                break;
            }
        }

        if (!messages.get(endIndex).endOfEntry()) {
            //find end of msg
            for (int i = endIndex + 1; i < messages.size(); i++) {
                ChatHudLine.Visible chatHudLine = messages.get(i);
                if (chatHudLine.endOfEntry()) {
                    endIndex = i - 1;
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = endIndex; i >= startIndex; i--) {
            ChatHudLine.Visible chatHudLine = messages.get(i);
            TextUtil.acceptOrderedText(builder, chatHudLine.content());
        }
        return builder.toString();
    }
}
