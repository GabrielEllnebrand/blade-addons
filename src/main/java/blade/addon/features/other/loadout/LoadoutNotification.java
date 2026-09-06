package blade.addon.features.other.loadout;

import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.ExtraOptions;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class LoadoutNotification extends CombineableNotification {

    public LoadoutNotification() {
        super("Loadout notification");
    }

    @Override
    public boolean enabled() {
        return ExtraOptions.enableLoadoutNotification;
    }

    @Override
    public Component getText() {
        return Component.literal("§a" + LoadoutData.getCurrentLoadout());
    }

    @Override
    public boolean shouldRender() {
        return LoadoutData.shouldDisplayNotification();
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }
}
