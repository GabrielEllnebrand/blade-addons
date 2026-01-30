package blade.addon.features.filter;

import blade.addon.features.notifications.Notifications;
import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class FilterList extends Screen {

    private static final int TITLE_COLOR = 0xffffffff;
    private static final float TITLE_SCALAR = 1.5f;
    private static final int TITLE_Y_OFFSET = 20;

    private static final int BUTTON_HEIGHT = 30;

    private final Screen parent;
    private final ConfigScroll scroll;
    private final ButtonWidget addFilter;

    public FilterList() {
        super(Text.literal("Filter"));

        MinecraftClient client = MinecraftClient.getInstance();
        parent = client.currentScreen;
        Window window = client.getWindow();

        addFilter = ButtonWidget.builder(Text.literal("Add Filter"), this::addFilter).position((window.getScaledWidth() - Constants.WIDGET_WIDTH) / 2,  TITLE_Y_OFFSET + 16).width(100).build();
        scroll = new ConfigScroll(0,  BUTTON_HEIGHT + TITLE_Y_OFFSET + 16, window.getScaledWidth(), window.getScaledHeight() - BUTTON_HEIGHT, Constants.WIDGET_WIDTH);
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
    protected void init() {
        super.init();
        this.addDrawableChild(scroll);
        this.addDrawableChild(addFilter);
        updateList();
    }

    private void updateList() {
        scroll.children().clear();

        for (int i = 0; i < Filters.filters.size(); i++) {
            int finalI = i;
            scroll.add(new FilterEntry(() -> Filters.filters.get(finalI), string -> {
                //race condition check
                if (finalI >= Filters.filters.size()) return;
                Filters.filters.set(finalI, string);
            }, finalI, this));
        }

        scroll.update();
        scroll.setScrollY(0);
    }

    @Override
    public void close() {
        assert this.client != null;
        Notifications.save();
        this.client.setScreen(this.parent);
        Filters.filterManager.save();
    }

    private void addFilter(ButtonWidget buttonWidget) {
        Filters.filters.add("");
        updateList();
    }

    public void removeFilter(int index) {
        if (Filters.filters.size() <= index) return;
        Filters.filters.remove(index);
        updateList();
    }

}
