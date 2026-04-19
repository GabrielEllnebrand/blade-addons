package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void click(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {

        if (!ExtraOptions.copyChat || click.button() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ChatHud chatHud = mc.inGameHud.getChatHud();
        ChatHudAccessor hudAccessor = (ChatHudAccessor) chatHud;
        if (chatHud == null) return;

        if (hudAccessor.width() + 4 < click.x()) return;

        final int n = (int) (9 * (mc.options.getChatLineSpacing().getValue() + 1.0));
        int index = (int) (MathHelper.floor((mc.getWindow().getScaledHeight() - 40) / hudAccessor.getScale()) - click.y()) / n + hudAccessor.getScrolledLines();

        List<ChatHudLine.Visible> messages = hudAccessor.getVisibleMessages();
        if (index < 0 || index >= messages.size()) return;

        String string;
        if (ExtraOptions.copyLineOnly) {
            ChatHudLine.Visible msg = messages.get(index);
            string = TextUtil.orderedTextToString(msg.content());
        } else {
            string = copyChat(messages, index);
        }

        if (string == null) return;

        if (ExtraOptions.removeColorCodes) {
            string = string.replaceAll("§.", "");
        }

        if (ExtraOptions.replaceColorChars) {
            string = string.replace("§", "&");
        }

        mc.keyboard.setClipboard(string);

        if (ExtraOptions.copyChatFeedback) {
            Misc.addChatMessage(Text.literal("Copied chat message"));
        }

    }

    @Unique
    private static String copyChat(List<ChatHudLine.Visible> messages, int index) {
        int endIndex = index;

        if (messages == null || endIndex < 0 || endIndex >= messages.size()) return null;

        int startIndex = endIndex;


        //edge case where the index is end of entry
        //but there are lines above that needs to be included
        ChatHudLine.Visible currentLine = messages.get(endIndex);
        if (endIndex + 1 < messages.size() && currentLine.endOfEntry() && !messages.get(startIndex + 1).endOfEntry()) {
            endIndex++;
        }


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


    //final int l = 9;
    //double d = this.client.options.getChatLineSpacing().getValue();
    //final int k = MathHelper.floor((windowHeight - 40) / f);
    //final int n = (int)(l * (d + 1.0));
    //int lx = k - y * n;
    //float f = (float)this.getChatScale();

    //
    //
    //MATH PART
    //mx <= click.y <= lx
    //lx + y * n = k
    //(k - lx) / n = y = forEachVisibleLine.k
    //lineIndex = forEachVisibleLine.l = forEachVisibleLine.k + this.scrolledLines

}





