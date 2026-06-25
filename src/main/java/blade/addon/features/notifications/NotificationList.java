package blade.addon.features.notifications;

import com.mojang.blaze3d.platform.Window;
import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class NotificationList extends Screen {

    private static final int TITLE_COLOR = 0xffffffff;
    private static final float TITLE_SCALAR = 1.5f;
    private static final int TITLE_Y_OFFSET = 20;

    private static final int BUTTON_HEIGHT = 30;

    private final Screen parent;
    private final ConfigScroll scroll;
    private final Button addNotification;

    public NotificationList() {
        super(Component.literal("Notifications"));

        Minecraft client = Minecraft.getInstance();
        parent = client.screen;
        Window window = client.getWindow();

        scroll = new ConfigScroll(0,  BUTTON_HEIGHT + TITLE_Y_OFFSET + 16, window.getGuiScaledWidth(), window.getGuiScaledHeight() - BUTTON_HEIGHT, Constants.WIDGET_WIDTH);
        addNotification = Button.builder(Component.literal("Add notification"), this::addNotification).pos((window.getGuiScaledWidth() - Constants.WIDGET_WIDTH) / 2,  TITLE_Y_OFFSET + 16).width(100).build();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(scroll);
        this.addRenderableWidget(addNotification);
        updateList();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

        float centerX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - (this.font.width(this.title) * TITLE_SCALAR)) / 2;
        Matrix3x2fStack stack = graphics.pose();
        stack.pushMatrix();
        stack.translate(centerX, TITLE_Y_OFFSET);
        stack.scale(TITLE_SCALAR, TITLE_SCALAR);
        graphics.text(this.font, this.title, 0, 0, TITLE_COLOR, true);
        stack.popMatrix();
    }

    @Override
    public void onClose() {
        Notifications.save();
        this.minecraft.setScreen(this.parent);
    }

    private void updateList() {
        scroll.children().clear();

        List<Notification> notifications = Notifications.getNotifications();
        for (Notification notification : notifications) {
            NotificationEntry entry = new NotificationEntry(this, notification);
            scroll.add(entry);
        }

        scroll.update();
        scroll.setScrollAmount(0);
    }

    private void addNotification(Button buttonWidget) {
        Notification notification = new Notification();
        Notifications.addNotification(notification);
        updateList();
    }

    public void removeNotification(Notification notification) {
        Notifications.removeNotification(notification);
        updateList();
    }
}
