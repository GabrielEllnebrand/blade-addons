package blade.addon.mixin;

import blade.addon.features.dungeon.f7.invincibility.MaskHolder;
import blade.addon.features.dungeon.f7.invincibility.MaskType;
import blade.addon.features.item.ItemRarity;
import blade.addon.features.item.ItemRarityHolder;
import blade.addon.features.item.PetHolder;
import blade.addon.features.item.ProtectedItemHolder;
import blade.addon.features.item.StarCountHolder;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemRarityHolder, PetHolder, StarCountHolder, ProtectedItemHolder, MaskHolder {

    @Unique
    private ItemRarity itemRarity = null;

    @Unique
    boolean selectedPet = false;

    @Unique
    boolean scannedPet = false;

    @Unique
    boolean scannedStars = false;

    @Unique
    int starCount = 0;

    @Unique
    boolean scannedProtected = false;

    @Unique
    boolean isItemProtected = false;

    @Unique
    MaskType maskType = null;

    @Unique
    @Override
    public ItemRarity blade_addons$getItemRarity() {
        return itemRarity;
    }

    @Unique
    @Override
    public boolean blade_addons$hasItemRarity() {
        return itemRarity != ItemRarity.NONE;
    }

    @Unique
    @Override
    public void blade_addons$setItemRarity(ItemRarity itemRarity) {
        this.itemRarity = itemRarity;
    }

    @Unique
    @Override
    public boolean blade_addons$hasScanned() {
        return itemRarity != null;
    }

    @Unique
    @Override
    public boolean blade_addons$hasScannedPet() {
        return scannedPet;
    }

    @Unique
    @Override
    public void blade_addons$setSelected(boolean selected) {
        selectedPet = selected;
        scannedPet = true;
    }

    @Unique
    @Override
    public boolean blade_addons$isSelected() {
        return selectedPet;
    }

    @Unique
    @Override
    public boolean blade_addons$hasScannedStars() {
        return scannedStars;
    }

    @Unique
    @Override
    public void blade_addons$setStarCount(int count) {
        starCount = count;
        scannedStars = true;
    }

    @Unique
    @Override
    public int blade_addons$getStarCount() {
        return starCount;
    }

    @Unique
    @Override
    public boolean blade_addons$hasScannedProtection() {
        return scannedProtected;
    }

    @Unique
    @Override
    public void blade_addons$setProtected(boolean value) {
        scannedProtected = true;
        isItemProtected = value;
    }

    @Unique
    @Override
    public boolean blade_addons$isProtected() {
        return isItemProtected;
    }

    @Unique
    @Override
    public boolean blade_addons$scannedMask() {
        return maskType != null;
    }

    @Unique
    @Override
    public void blade_addons$setMask(MaskType maskType) {
        this.maskType = maskType;
    }

    @Unique
    @Override
    public MaskType blade_addons$getMask() {
        return maskType;
    }
}
