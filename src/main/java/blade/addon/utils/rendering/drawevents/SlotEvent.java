package blade.addon.utils.rendering.drawevents;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public interface SlotEvent {

    void draw(DrawContext drawContext, ItemStack item, int x, int y);

}
