package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.utils.data.ItemUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class MaskHighlight {

    private static final int COLOR = 0xbb666666;

    public static void draw(DrawContext context, ItemStack stack, int x, int y) {
        MaskHolder holder = (MaskHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$scannedMask()) {
            MaskType mask = getMaskType(stack);
            holder.blade_addons$setMask(mask);
        }

        MaskType mask = holder.blade_addons$getMask();

        int progress = 0;
        switch (mask) {
            case SPIRIT -> progress = (int) (InvincibilityTimer.getSpiritProgress() * 16);
            case BONZO -> progress = (int) (InvincibilityTimer.getBonzoProgress() * 16);
        }
        if (progress == 0) return;
        context.fill(x, y + 16 - progress, x + 16, y + 16, COLOR);
    }

    public static MaskType getMaskType(ItemStack item) {
        String id = ItemUtil.getId(item);
        if (id == null) return MaskType.NONE;

        if (id.contains("SPIRIT_MASK")) return MaskType.SPIRIT;
        if (id.contains("BONZO_MASK")) return MaskType.BONZO;
        return MaskType.NONE;
    }

}
