package blade.addon.features.dungeon.f7.terms.device;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Floor7;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DeviceNotification extends CombineableNotification {

    public DeviceNotification() {
        super("Device done notification");
    }

    @Override
    public boolean enabled() {
        return  Floor7.notifyPre4Completion || Floor7.notifySSCompletion;
    }

    @Override
    public Component getText() {
        return Component.literal("§aDevice Completed!");
    }

    @Override
    public boolean shouldRender() {
        return DeviceNotifier.shouldDisplay();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }
}
