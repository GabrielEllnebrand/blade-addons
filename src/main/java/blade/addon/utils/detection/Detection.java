package blade.addon.utils.detection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class Detection {


    public static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            PlayerDataHolder dataHolder = (PlayerDataHolder) (Object) player;
            return dataHolder.blade_addons$isRealPlayer();

        }
        return false;
    }
}
