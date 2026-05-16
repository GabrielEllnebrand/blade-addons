package blade.addon.features.item;

import blade.addon.utils.config.values.Visual;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class StarCountHighlight {

    public static void init() {
        DrawEvents.INVENTORY_SLOT_AFTER.register(StarCountHighlight::draw);
        DrawEvents.HUD_SLOT_AFTER.register(StarCountHighlight::draw);
    }

    private static void draw(GuiGraphics context, ItemStack stack, int x, int y) {

        if (!Visual.drawStarCount) return;

        StarCountHolder holder = (StarCountHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedStars()) {
            holder.blade_addons$setStarCount(getStarCount(stack));
        }

        int starCount = holder.blade_addons$getStarCount();
        if (starCount == 0) return;


        Font textRenderer = Minecraft.getInstance().font;
        String starText = "" + starCount;
        context.drawString(Minecraft.getInstance().font, starText, x + 17 - textRenderer.width(starText), y + 18 - textRenderer.lineHeight, 0xffffffff, true);
    }

    public static int getStarCount(ItemStack item) {
        CustomData nbt = item.get(DataComponents.CUSTOM_DATA);
        if (nbt == null)  return 0;

        CompoundTag compound = nbt.copyTag();

        int starCount = compound.getIntOr("upgrade_level", 0);
        if (starCount == 0) {
            //the old star system they used to use.
            // Some items don't have the new data so this is a fallback
            starCount = compound.getIntOr("dungeon_item_level", 0);
        }

        return starCount;
    }
}
