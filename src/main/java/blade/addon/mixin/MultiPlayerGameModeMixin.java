package blade.addon.mixin;

import blade.addon.features.other.pet.SelectedPet;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "handleContainerInput", at=@At("HEAD"))
    public void handleInventoryMouseClick(int container, int slotId, int button, ContainerInput containerInput, Player player, CallbackInfo ci) {
        SelectedPet.testLoadoutClick(container, slotId, button, containerInput, player);
    }

}
