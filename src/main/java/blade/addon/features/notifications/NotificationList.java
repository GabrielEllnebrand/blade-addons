package blade.addon.features.notifications;

import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class NotificationList extends Screen {

    private static final int TITLE_COLOR = 0xffffffff;
    private static final float TITLE_SCALAR = 1.5f;
    private static final int TITLE_Y_OFFSET = 20;

    private static final int BUTTON_HEIGHT = 30;

    private final Screen parent;
    private final ConfigScroll scroll;
    private final ButtonWidget addNotification;

    public NotificationList() {
        super(Text.literal("Notifications"));

        MinecraftClient client = MinecraftClient.getInstance();
        parent = client.currentScreen;
        Window window = client.getWindow();

        scroll = new ConfigScroll(0,  BUTTON_HEIGHT + TITLE_Y_OFFSET + 16, window.getScaledWidth(), window.getScaledHeight() - BUTTON_HEIGHT, Constants.WIDGET_WIDTH);
        addNotification = ButtonWidget.builder(Text.literal("Add notification"), this::addNotification).position((window.getScaledWidth() - Constants.WIDGET_WIDTH) / 2,  TITLE_Y_OFFSET + 16).width(100).build();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(scroll);
        this.addDrawableChild(addNotification);
        updateList();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        float centerX = (MinecraftClient.getInstance().getWindow().getScaledWidth() - (this.textRenderer.getWidth(this.title) * TITLE_SCALAR)) / 2;
        Matrix3x2fStack stack = context.getMatrices();
        stack.pushMatrix();
        stack.translate(centerX, TITLE_Y_OFFSET);
        stack.scale(TITLE_SCALAR, TITLE_SCALAR);
        context.drawText(this.textRenderer, this.title, 0, 0, TITLE_COLOR, true);
        stack.popMatrix();
    }

    @Override
    public void close() {
        assert this.client != null;
        Notifications.save();
        this.client.setScreen(this.parent);
    }

    private void updateList() {
        scroll.children().clear();

        List<Notification> notifications = Notifications.getNotifications();
        for (Notification notification : notifications) {
            NotificationEntry entry = new NotificationEntry(this, notification);
            scroll.add(entry);
        }

        scroll.update();
        scroll.setScrollY(0);
    }

    private void addNotification(ButtonWidget buttonWidget) {
        Notification notification = new Notification();
        Notifications.addNotification(notification);
        updateList();
    }

    public void removeNotification(Notification notification) {
        Notifications.removeNotification(notification);
        updateList();
    }
}
