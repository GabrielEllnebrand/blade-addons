package blade.addon.features.item;

public interface PetHolder {

    boolean selectedPet = false;
    boolean scannedPet = false;

    boolean blade_addons$hasScannedPet();

    void blade_addons$setSelected(boolean selected);

    boolean blade_addons$isSelected();

}
