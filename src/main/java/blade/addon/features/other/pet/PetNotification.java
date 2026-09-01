package blade.addon.features.other.pet;

import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.ExtraOptions;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PetNotification extends CombineableNotification {
    public PetNotification() {
        super("Pet swap notification");
    }

    @Override
    public boolean enabled() {
        return ExtraOptions.sendPetSwapNotification;
    }

    @Override
    public Component getText() {
        return SelectedPet.currentPetText;
    }

    @Override
    public boolean shouldRender() {
        return SelectedPet.displayNotification();
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }
}
