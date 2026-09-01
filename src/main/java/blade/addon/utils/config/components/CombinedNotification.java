package blade.addon.utils.config.components;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CombinedNotification extends HUDComponent {

    private final CombineableNotification[] notifications;

    public CombinedNotification(CombineableNotification... notifications) {
        super("Combined Notifications");
        this.notifications = notifications;
    }

    @Override
    public int getWidth() {
        return 130;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Dungeons.combineScreenNotifications;
    }

    @Override
    public boolean shouldRender() {
        for (CombineableNotification notification : notifications) {
            if (notification.shouldRender()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HUDCategory> categories() {
        List<HUDCategory> categories = new ArrayList<>();

        for (CombineableNotification notification : notifications) {
            if (notification.enabled()) {
                List<HUDCategory> temp = notification.categories();
                if (temp == null) return null;
                categories.addAll(notification.categories());
            }
        }

        return categories;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        for (CombineableNotification notification : notifications) {
            if (notification.shouldRender()) {
                notification.draw(guiGraphicsExtractor, getScaledX(), getScaledY());
                return;
            }
        }
    }

    @Override
    public void renderEditTemplate(@NonNull GuiGraphicsExtractor graphics) {
        RenderUtils.drawCenteredText(graphics, Minecraft.getInstance().font, Component.literal("Some notification"), getScaledX(), getScaledY(), 130, 0xffffffff);

    }
}
