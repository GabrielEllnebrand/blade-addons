package blade.addon.mixin;

import blade.addon.utils.interfaces.ArmourStandHolder;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin implements ArmourStandHolder {

    @Unique
    private boolean scanned = false;

    @Override
    public boolean blade_addons$hasBeenScanned() {
        return scanned;
    }

    @Override
    public void blade_addons$setScanned(boolean value) {
        scanned = value;
    }
}
