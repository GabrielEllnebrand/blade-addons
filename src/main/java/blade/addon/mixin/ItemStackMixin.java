package blade.addon.mixin;

import blade.addon.features.item.ItemRarityHolder;
import blade.addon.features.item.ItemRarity;
import blade.addon.features.item.PetHolder;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemRarityHolder, PetHolder {

    @Unique
    private ItemRarity itemRarity = null;

    @Unique
    boolean selectedPet = false;

    @Unique
    boolean scannedPet = false;


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
}
