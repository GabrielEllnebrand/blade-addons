package blade.addon.features.item;

public interface ItemRarityHolder {

    ItemRarity itemRarity = ItemRarity.NONE;

    ItemRarity blade_addons$getItemRarity();

    boolean blade_addons$hasItemRarity();

    void blade_addons$setItemRarity(ItemRarity itemRarity);

    boolean blade_addons$hasScanned();
}
