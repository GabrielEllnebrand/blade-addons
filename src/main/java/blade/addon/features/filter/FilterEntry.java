package blade.addon.features.filter;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilterEntry extends TextFieldWidget {

    private static final Identifier CROSS = Identifier.of(blade.addon.utils.Constants.NAMESPACE, "cross");

    private static final int HEIGHT = 20;
    private static final int INPUT_COLOR = 0xff222222;

    private static final int SPRITE_SIZE = 16;
    private static final int SPRITE_WIDTH_AREA = SPRITE_SIZE + 10;
    private int maxLength;
    private int selectionEnd;
    private Consumer<String> changedListener;

    private final int index;
    private final FilterList parent;

    public FilterEntry(Supplier<String> supplier, Consumer<String> consumer, int index, FilterList parent) {
        super(MinecraftClient.getInstance().textRenderer, Constants.WIDGET_WIDTH, HEIGHT, Text.empty());
        this.setMaxLength(100);
        this.setText(supplier.get());
        this.setChangedListener(consumer);
        this.index = index;
        this.parent = parent;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        DrawHelper.drawBackground(context, getX(), super.getY(), width - SPRITE_WIDTH_AREA, height, INPUT_COLOR);
        Pair<Integer, Integer> pos = getRemovePos();
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CROSS, pos.getLeft(), pos.getRight(), SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (!this.isActive()) {
            return false;
        } else if (input.isValidChar() || isValid(input)) {
            this.write(input.asString());
            return true;
        } else {
            return false;
        }
    }


    private boolean isValid(CharInput input) {
        return input.asString().contains("§");
    }

    /**
     * A slightly modified version of the TextFieldWidget write(String)
     * because it removes chars like §
     *
     * @param text The text to add
     */
    @Override
    public void write(String text) {
        int i = Math.min(getCursor(), this.selectionEnd);
        int j = Math.max(getCursor(), this.selectionEnd);
        int k = this.maxLength - getText().length() - (i - j);
        if (k > 0) {
            //String string = StringHelper.stripInvalidChars(text);
            String string = text;
            int l = string.length();
            if (k < l) {
                if (Character.isHighSurrogate(string.charAt(k - 1))) {
                    k--;
                }

                string = string.substring(0, k);
                l = k;
            }
            String string2 = new StringBuilder(getText()).replace(i, j, string).toString();
            setText(string2);
            this.setSelectionStart(i + l);
            this.setSelectionEnd(getCursor());
            changedListener.accept(getText());
        }
    }

    @Override
    public void setSelectionEnd(int index) {
        this.selectionEnd = MathHelper.clamp(index, 0, getText().length());
        super.setSelectionEnd(index);
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        super.setMaxLength(maxLength);
    }

    @Override
    public void setChangedListener(Consumer<String> changedListener) {
        this.changedListener = changedListener;
        super.setChangedListener(changedListener);
    }

    @Override
    public int getY() {
        return super.getY();
    }

    @Override
    public int getInnerWidth() {
        return width - SPRITE_WIDTH_AREA - 8;
    }

    @Override
    public boolean drawsBackground() {
        return false;
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
            parent.removeFilter(index);
            return;
        }

        //make sure you cant click around the button accidentally
        if (width + getX() - SPRITE_WIDTH_AREA < x) return;
        super.onClick(click, doubled);
    }
}
