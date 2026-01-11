package blade.addon.features.notifications;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

class NotificationEntry extends ClickableWidget {

    private static final int PADDING = 5;
    private static final int TEXT_SPACE = 100;
    private static final int MAX_TEXT_WIDTH = 75;
    private static final int BUTTON_WIDTH = 60;


    private final NotificationList parent ;
    private final Notification notification;

    public NotificationEntry(NotificationList parent, Notification notification) {
        super(0, 0, Constants.WIDGET_WIDTH, 30, Text.empty());
        this.parent = parent;
        this.notification = notification;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DrawHelper.drawBackground(context, x, y, width, height);

        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;

        drawText(context, textRenderer, notification.getMatchString(), x + PADDING, y);
        drawText(context, textRenderer, notification.getNotificationString(), x + PADDING * 2 + TEXT_SPACE, y);

        int buttonWidth = 60;
        String remove = "Remove";
        textRenderer.trimToWidth(remove, TEXT_SPACE - 2);
        DrawHelper.drawBackground(context, x + width - buttonWidth - PADDING, y + 4, buttonWidth, height - 8);
        context.drawText(textRenderer, remove, x + width - buttonWidth, y + (30 - Constants.TEXT_HEIGHT) / 2, 0xffffffff, true);
    }

    private void drawText(DrawContext context, TextRenderer textRenderer, String string, int x, int y) {

        Text text;
        if (string.isEmpty()) {
            text = Text.literal("Empty");
        } else {
            text = Text.literal(textRenderer.trimToWidth(string, MAX_TEXT_WIDTH - 2));
        }

        int textY = (30 - Constants.TEXT_HEIGHT) / 2 + y;

        context.enableScissor(x, y, x + MAX_TEXT_WIDTH - 1, y + height);
        context.drawText(textRenderer, text, x + 1, textY, 0xffffffff, true);
        context.disableScissor();
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        double x = click.x();
        double y = click.y();

        if (x >= getX() + width - BUTTON_WIDTH - PADDING && x <= getX() + width + BUTTON_WIDTH && y >= getY() + 4 && y <= getY() + height - 4) {
            parent.removeNotification(notification);
            return;
        }

        MinecraftClient.getInstance().setScreen(new NotificationEditScreen(notification));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
