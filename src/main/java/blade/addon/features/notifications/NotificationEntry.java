package blade.addon.features.notifications;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;

class NotificationEntry extends AbstractWidget {

    private static final Identifier CROSS = Identifier.fromNamespaceAndPath(blade.addon.utils.Constants.NAMESPACE, "cross");

    private static final int PADDING = 5;
    private static final int TEXT_SPACE = 125;
    private static final int MAX_TEXT_WIDTH = 100;

    private static final int SPRITE_SIZE = 16;
    private static final int SPRITE_WIDTH_AREA = SPRITE_SIZE + 10;

    private final NotificationList parent ;
    private final Notification notification;

    public NotificationEntry(NotificationList parent, Notification notification) {
        super(0, 0, Constants.WIDGET_WIDTH, 28, Component.empty());
        this.parent = parent;
        this.notification = notification;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DrawHelper.drawBackground(context, x, y, width - SPRITE_WIDTH_AREA, height);

        Minecraft mc = Minecraft.getInstance();
        Font textRenderer = mc.font;

        drawText(context, textRenderer, notification.getMatchString(), x + PADDING, y);
        drawText(context, textRenderer, notification.getNotificationString(), x + PADDING * 2 + TEXT_SPACE, y);

        Tuple<Integer, Integer> pos = getRemovePos();
        context.blitSprite(RenderPipelines.GUI_TEXTURED, CROSS, pos.getA(), pos.getB(), SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
    }

    private void drawText(GuiGraphics context, Font textRenderer, String string, int x, int y) {

        Component text;
        if (string.isEmpty()) {
            text = Component.literal("Empty");
        } else {
            text = Component.literal(textRenderer.plainSubstrByWidth(string, MAX_TEXT_WIDTH - 2));
        }

        int textY = (30 - Constants.TEXT_HEIGHT) / 2 + y;

        context.enableScissor(x, y, x + MAX_TEXT_WIDTH - 1, y + height);
        context.drawString(textRenderer, text, x + 1, textY, 0xffffffff, true);
        context.disableScissor();
    }

    private Tuple<Integer, Integer> getRemovePos() {
        return new Tuple<>(getX() + width - SPRITE_SIZE - 5, getY() + (height - SPRITE_SIZE) / 2);
    }

    private boolean inRemovalBounds(double x, double y) {
        Tuple<Integer, Integer> pos = getRemovePos();
        return x >= pos.getA() && x <=  pos.getA() + SPRITE_SIZE && y >= pos.getB() && y <= pos.getB() + SPRITE_SIZE;
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        double x = click.x();
        double y = click.y();

        if (inRemovalBounds(x, y)) {
            parent.removeNotification(notification);
            return;
        }

        //make sure you cant click around the button accidentally
        if (width + getX() - SPRITE_WIDTH_AREA < x) return;

        Minecraft.getInstance().setScreen(new NotificationEditScreen(notification));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
