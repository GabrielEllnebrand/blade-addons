package blade.addon.features.notifications;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

class NotificationEntry extends ClickableWidget {

    private static final Identifier CROSS = Identifier.of(blade.addon.utils.Constants.NAMESPACE, "cross");

    private static final int PADDING = 5;
    private static final int TEXT_SPACE = 125;
    private static final int MAX_TEXT_WIDTH = 100;

    private static final int SPRITE_SIZE = 16;
    private static final int SPRITE_WIDTH_AREA = SPRITE_SIZE + 10;

    private final NotificationList parent ;
    private final Notification notification;

    public NotificationEntry(NotificationList parent, Notification notification) {
        super(0, 0, Constants.WIDGET_WIDTH, 28, Text.empty());
        this.parent = parent;
        this.notification = notification;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DrawHelper.drawBackground(context, x, y, width - SPRITE_WIDTH_AREA, height);

        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;

        drawText(context, textRenderer, notification.getMatchString(), x + PADDING, y);
        drawText(context, textRenderer, notification.getNotificationString(), x + PADDING * 2 + TEXT_SPACE, y);

        Pair<Integer, Integer> pos = getRemovePos();
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CROSS, pos.getLeft(), pos.getRight(), SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
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

    private Pair<Integer, Integer> getRemovePos() {
        return new Pair<>(getX() + width - SPRITE_SIZE - 5, getY() + (height - SPRITE_SIZE) / 2);
    }

    private boolean inRemovalBounds(double x, double y) {
        Pair<Integer, Integer> pos = getRemovePos();
        return x >= pos.getLeft() && x <=  pos.getLeft() + SPRITE_SIZE && y >= pos.getRight() && y <= pos.getRight() + SPRITE_SIZE;
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        double x = click.x();
        double y = click.y();

        if (inRemovalBounds(x, y)) {
            parent.removeNotification(notification);
            return;
        }

        //make sure you cant click around the button accidentally
        if (width + getX() - SPRITE_WIDTH_AREA < x) return;

        MinecraftClient.getInstance().setScreen(new NotificationEditScreen(notification));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
