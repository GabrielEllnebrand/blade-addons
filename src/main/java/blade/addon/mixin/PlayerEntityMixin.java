package blade.addon.mixin;

import blade.addon.utils.interfaces.PlayerDataHolder;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerDataHolder {

    @Unique
    boolean realPlayer = false;

    @Override
    public void blade_addons$setIsRealPlayer(boolean value) {
        realPlayer = value;
    }

    @Override
    public boolean blade_addons$isRealPlayer() {
        return realPlayer;
    }
}
