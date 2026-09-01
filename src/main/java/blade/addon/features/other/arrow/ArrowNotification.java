package blade.addon.features.other.arrow;

import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.ExtraOptions;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ArrowNotification extends CombineableNotification {

    public ArrowNotification() {
        super("Selected arrow title");
    }

    @Override
    public boolean enabled() {
        return ExtraOptions.arrowSwapNotification;
    }

    @Override
    public Component getText() {
        return ArrowSwapper.displayedText;
    }

    @Override
    public boolean shouldRender() {
        return ArrowSwapper.displayNotification();
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }
}
