package blade.addon.features.item;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class SelectedPetHighlight {

    public static void init() {
        DrawEvents.INVENTORY_SLOT_BEFORE.register(SelectedPetHighlight::draw);
    }

    private static void draw(DrawContext context, ItemStack stack, int x, int y) {
        if (!ExtraOptions.highlightSelectedPet) return;

        PetHolder holder = (PetHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedPet()) {
            holder.blade_addons$setSelected(isSelected(stack));
        }

        if (!holder.blade_addons$isSelected()) return;

        context.fill(x, y, x + 16, y + 16, ExtraOptions.petHighlightColor);
    }

    public static boolean isSelected(ItemStack item) {
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null) return false;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for (Text text : lines) {
            if (text == null) continue;
            if (text.getString().equals("Click to despawn!")) return true;
        }
        return false;
    }
}
