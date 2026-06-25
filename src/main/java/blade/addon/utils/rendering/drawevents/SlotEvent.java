package blade.addon.utils.rendering.drawevents;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public interface SlotEvent {

    void draw(GuiGraphicsExtractor drawContext, ItemStack item, int x, int y);

}
