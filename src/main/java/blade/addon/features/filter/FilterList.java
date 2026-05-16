package blade.addon.features.filter;

import blade.addon.features.notifications.Notifications;
import com.mojang.blaze3d.platform.Window;
import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import org.joml.Matrix3x2fStack;

public class FilterList extends Screen {

    private static final int TITLE_COLOR = 0xffffffff;
    private static final float TITLE_SCALAR = 1.5f;
    private static final int TITLE_Y_OFFSET = 20;

    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_WIDTH = 100;

    private static final Component INFO_TEXT = Component.literal("To add a filter, input a regex which would match the word fully. I'd highly recommend using regex101.com to create them.");


    private final Screen parent;
    private final ConfigScroll scroll;
    private final Button addFilter;

    public FilterList() {
        super(Component.literal("Filter"));

        Minecraft client = Minecraft.getInstance();
        parent = client.screen;
        Window window = client.getWindow();

        Tuple<Integer, Integer> pos = getButtonPos();
        addFilter = Button.builder(Component.literal("Add Filter"), this::addFilter).pos(pos.getA(), pos.getB()).width(BUTTON_WIDTH).build();
        scroll = new ConfigScroll(0, BUTTON_HEIGHT + TITLE_Y_OFFSET + 16, window.getGuiScaledWidth(), window.getGuiScaledHeight() - BUTTON_HEIGHT, Constants.WIDGET_WIDTH);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        float centerX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - (this.font.width(this.title) * TITLE_SCALAR)) / 2;
        Matrix3x2fStack stack = context.pose();
        stack.pushMatrix();
        stack.translate(centerX, TITLE_Y_OFFSET);
        stack.scale(TITLE_SCALAR, TITLE_SCALAR);
        context.drawString(this.font, this.title, 0, 0, TITLE_COLOR, true);
        stack.popMatrix();

        Tuple<Integer, Integer> pos = getButtonPos();
        context.drawWordWrap(this.font, INFO_TEXT, pos.getA() + BUTTON_WIDTH + 5, pos.getB(), 245, 0xffffffff, true);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(scroll);
        this.addRenderableWidget(addFilter);
        updateList();
    }

    private Tuple<Integer, Integer> getButtonPos() {
        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();
        return new Tuple<>((window.getGuiScaledWidth() - Constants.WIDGET_WIDTH) / 2, TITLE_Y_OFFSET + 16);
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
        scroll.setScrollAmount(0);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        Notifications.save();
        this.minecraft.setScreen(this.parent);
        Filters.filterManager.save();
    }

    private void addFilter(Button buttonWidget) {
        Filters.filters.add("");
        updateList();
    }

    public void removeFilter(int index) {
        if (Filters.filters.size() <= index) return;
        Filters.filters.remove(index);
        updateList();
    }

}
