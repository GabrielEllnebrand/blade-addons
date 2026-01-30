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

    @Override
    public ItemRarity blade_addons$getItemRarity() {
        return itemRarity;
    }

    @Override
    public boolean blade_addons$hasItemRarity() {
        return itemRarity != ItemRarity.NONE;
    }

    @Override
    public void blade_addons$setItemRarity(ItemRarity itemRarity) {
        this.itemRarity = itemRarity;
    }

    @Override
    public boolean blade_addons$hasScanned() {
        return itemRarity != null;
    }

    @Override
    public boolean blade_addons$hasScannedPet() {
        return scannedPet;
    }

    @Override
    public void blade_addons$setSelected(boolean selected) {
        selectedPet = selected;
        scannedPet = true;
    }

    @Override
    public boolean blade_addons$isSelected() {
        return selectedPet;
    }

    @Override
    public boolean blade_addons$hasScannedStars() {
        return scannedStars;
    }

    @Override
    public void blade_addons$setStarCount(int count) {
        starCount = count;
        scannedStars = true;
    }

    @Override
    public int blade_addons$getStarCount() {
        return starCount;
    }

    @Override
    public boolean blade_addons$hasScannedProtection() {
        return scannedProtected;
    }

    @Override
    public void blade_addons$setProtected(boolean value) {
        scannedProtected = true;
        isItemProtected = value;
    }

    @Override
    public boolean blade_addons$isProtected() {
        return isItemProtected;
    }

    @Override
    public boolean blade_addons$scannedMask() {
        return maskType != null;
    }

    @Override
    public void blade_addons$setMask(MaskType maskType) {
        this.maskType = maskType;
    }

    @Override
    public MaskType blade_addons$getMask() {
        return maskType;
    }
}
