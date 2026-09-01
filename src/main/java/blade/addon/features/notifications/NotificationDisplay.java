package blade.addon.features.notifications;

import blade.addon.utils.config.components.CombineableNotification;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class NotificationDisplay extends CombineableNotification {

    public NotificationDisplay() {
        super("Chat notification");
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Component getText() {
        return Component.literal(Notifications.getMessage() != null ? Notifications.getMessage() : "Some notification");
    }

    @Override
    public boolean shouldRender() {
        return Notifications.tick > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }
}
