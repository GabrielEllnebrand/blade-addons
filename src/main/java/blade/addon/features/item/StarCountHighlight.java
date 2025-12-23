package blade.addon.features.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class StarCountHighlight {

    public static void draw(DrawContext context, ItemStack stack, int x, int y) {
        StarCountHolder holder = (StarCountHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedStars()) {
            holder.blade_addons$setStarCount(getStarCount(stack));
        }

        int starCount = holder.blade_addons$getStarCount();
        if (starCount == 0) return;


        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String starText = "" + starCount;
        context.drawText(MinecraftClient.getInstance().textRenderer, starText, x + 17 - textRenderer.getWidth(starText), y + 18 - textRenderer.fontHeight, 0xffffffff, true);
    }

    public static int getStarCount(ItemStack item) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null)  return 0;

        NbtCompound compound = nbt.copyNbt();

        int starCount = compound.getInt("upgrade_level", 0);
        if (starCount == 0) {
            //the old star system they used to use.
            // Some items don't have the new data so this is a fallback
            starCount = compound.getInt("dungeon_item_level", 0);
        }

        return starCount;
    }
}
