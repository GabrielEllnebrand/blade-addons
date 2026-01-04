package blade.addon.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudInvoker {

    @Invoker("toChatLineY")
    double lineY(double y);

    @Invoker("toChatLineX")
    double lineX(double X);

    @Invoker("getMessageLineIndex")
    int getLineIndex(double chatLineX, double chatLineY);

    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();
}
