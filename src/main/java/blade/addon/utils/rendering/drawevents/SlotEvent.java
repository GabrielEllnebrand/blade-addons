package blade.addon.utils.rendering.drawevents;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface SlotEvent {

    void draw(GuiGraphics drawContext, ItemStack item, int x, int y);

}
