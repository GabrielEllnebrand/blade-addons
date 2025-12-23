package blade.addon.features.item;

public interface StarCountHolder {

    boolean scannedStars = false;

    int starCount = 0;

    boolean blade_addons$hasScannedStars();

    void blade_addons$setStarCount(int count);

    int blade_addons$getStarCount();
}

