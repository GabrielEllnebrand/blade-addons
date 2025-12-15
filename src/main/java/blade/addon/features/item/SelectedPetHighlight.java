package blade.addon.features.item;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class SelectedPetHighlight {

    public static void draw(DrawContext context, ItemStack stack, int x, int y) {
        PetHolder holder = (PetHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedPet()) {
            holder.blade_addons$setSelected(isSelected(stack));
        }

        if (!holder.blade_addons$isSelected()) return;

        context.fill(x, y, x + 16, y + 16, 0xffff0000);
    }

    public static boolean isSelected(ItemStack item) {
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null)  return false;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for(Text text: lines) {
            if (text == null) continue;
            if (text.getString().equals("Click to despawn!")) return true;
        }
        return false;
    }
}
