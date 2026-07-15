package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.TextUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void click(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {

        if (!ExtraOptions.copyChat || click.button() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

        Minecraft mc = Minecraft.getInstance();
        ChatComponent chatHud = mc.gui.getChat();
        ChatComponentMixin hudAccessor = (ChatComponentMixin) chatHud;
        if (chatHud == null) return;

        if (hudAccessor.width() + 4 < click.x()) return;

        final int n = (int) (9 * (mc.options.chatLineSpacing().get() + 1.0) * hudAccessor.scale());
        int index = (int) (Mth.floor(mc.getWindow().getGuiScaledHeight() - 40) - click.y()) / n + hudAccessor.getScrolledLines();

        System.out.println(index + " " + hudAccessor.getScrolledLines());

        List<GuiMessage.Line> messages = hudAccessor.getVisibleMessages();
        if (index < 0 || index >= messages.size()) return;

        String string;
        if (ExtraOptions.copyLineOnly || click.hasShiftDown()) {
            GuiMessage.Line msg = messages.get(index);
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

        mc.keyboardHandler.setClipboard(string);

        if (ExtraOptions.copyChatFeedback) {
            Misc.addChatMessage(Component.literal("Copied chat message"));
        }

    }

    @Unique
    private static String copyChat(List<GuiMessage.Line> messages, int index) {
        int endIndex = index;

        if (messages == null || endIndex < 0 || endIndex >= messages.size()) return null;

        int startIndex = endIndex;


        //edge case where the index is end of entry
        //but there are lines above that needs to be included
        GuiMessage.Line currentLine = messages.get(endIndex);
        if (endIndex + 1 < messages.size() && currentLine.endOfEntry() && !messages.get(startIndex + 1).endOfEntry()) {
            endIndex++;
        }


        //find start of msg
        for (int i = endIndex; i >= 0; i--) {
            GuiMessage.Line chatHudLine = messages.get(i);
            if (chatHudLine.endOfEntry()) {
                startIndex = i;
                break;
            }
        }

        if (!messages.get(endIndex).endOfEntry()) {
            //find end of msg
            for (int i = endIndex + 1; i < messages.size(); i++) {
                GuiMessage.Line chatHudLine = messages.get(i);
                if (chatHudLine.endOfEntry()) {
                    endIndex = i - 1;
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = endIndex; i >= startIndex; i--) {
            GuiMessage.Line chatHudLine = messages.get(i);
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





