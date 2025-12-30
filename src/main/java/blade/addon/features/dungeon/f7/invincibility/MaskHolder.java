package blade.addon.features.dungeon.f7.invincibility;

public interface MaskHolder {

    MaskType maskType = null;

    boolean blade_addons$scannedMask();

    void blade_addons$setMask(MaskType maskType);

    MaskType blade_addons$getMask();
}
