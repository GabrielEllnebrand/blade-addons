package blade.addon.features.item;

public interface ProtectedItemHolder {

    boolean scannedProtected = false;

    boolean isItemProtected = false;

    boolean blade_addons$hasScannedProtection();

    void blade_addons$setProtected(boolean value);

    boolean blade_addons$isProtected();
}
