package blade.addon.features.item;

import blade.addon.utils.Misc;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ItemRarityDrawer {

    public static void draw(DrawContext context, ItemStack stack, int x, int y) {
        ItemRarityHolder holder = (ItemRarityHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScanned()) {
            ItemRarity rarity = Misc.getRarity(stack);
            holder.blade_addons$setItemRarity(rarity);
        }

        if (!holder.blade_addons$hasItemRarity()) return;

        ItemRarity rarity = holder.blade_addons$getItemRarity();
        context.fill(x, y, x + 16, y + 16, rarity.getColor());
    }
}
