package blade.addon.features.item;

import blade.addon.utils.config.values.Visual;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ItemRarityHighlight {

    public static void init() {
        DrawEvents.INVENTORY_SLOT_BEFORE.register(ItemRarityHighlight::draw);
        DrawEvents.HUD_SLOT_BEFORE.register(ItemRarityHighlight::draw);
    }

    private static void draw(DrawContext context, ItemStack stack, int x, int y) {
        if (!Visual.itemRarityBackground) return;
        ItemRarityHolder holder = (ItemRarityHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScanned()) {
            ItemRarity rarity = getRarity(stack);
            holder.blade_addons$setItemRarity(rarity);
        }

        if (!holder.blade_addons$hasItemRarity()) return;

        ItemRarity rarity = holder.blade_addons$getItemRarity();
        context.fill(x, y, x + 16, y + 16, rarity.getColor());
    }

    public static ItemRarity getRarity(ItemStack item) {
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null)  return ItemRarity.NONE;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return ItemRarity.NONE;

        for (Text line: lines.reversed()) {
            String string = line.getString();
            String[] rarityStrings = string.split(" ");

            for (String testString: rarityStrings) {
                try {
                    return ItemRarity.valueOf(testString);
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
        return ItemRarity.NONE;
    }
}
