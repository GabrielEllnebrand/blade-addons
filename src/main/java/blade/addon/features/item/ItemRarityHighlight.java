package blade.addon.features.item;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class ItemRarityHighlight {

    private static final Identifier NORMAL_BACKGROUND = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "rarity-background");
    private static final Identifier CIRCLE_BACKGROUND = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "rarity-background-circle");

    public static void init() {
        DrawEvents.INVENTORY_SLOT_BEFORE.register(ItemRarityHighlight::draw);
        DrawEvents.HUD_SLOT_BEFORE.register(ItemRarityHighlight::draw);
    }

    private static void draw(GuiGraphicsExtractor context, ItemStack stack, int x, int y) {
        if (!Visual.itemRarityBackground) return;
        ItemRarityHolder holder = (ItemRarityHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScanned()) {
            ItemRarity rarity = getRarity(stack);
            holder.blade_addons$setItemRarity(rarity);
        }

        if (!holder.blade_addons$hasItemRarity()) return;

        ItemRarity rarity = holder.blade_addons$getItemRarity();
        if (Visual.circularRarityBackground) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, CIRCLE_BACKGROUND, x, y, 16, 16, rarity.getColor());
        } else {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, NORMAL_BACKGROUND, x, y, 16, 16, rarity.getColor());
        }
    }

    public static ItemRarity getRarity(ItemStack item) {
        ItemLore lore = item.get(DataComponents.LORE);
        if (lore == null)  return ItemRarity.NONE;

        List<Component> lines = lore.lines();
        if (lines.isEmpty()) return ItemRarity.NONE;

        for (Component line: lines.reversed()) {
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
