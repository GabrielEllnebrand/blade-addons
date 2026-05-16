package blade.addon.features.filter;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;

public class FilterEntry extends EditBox {

    private static final Identifier CROSS = Identifier.fromNamespaceAndPath(blade.addon.utils.Constants.NAMESPACE, "cross");

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
        super(Minecraft.getInstance().font, Constants.WIDGET_WIDTH, HEIGHT, Component.empty());
        this.setMaxLength(200);
        this.setValue(supplier.get());
        this.setResponder(consumer);
        this.index = index;
        this.parent = parent;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        DrawHelper.drawBackground(context, getX(), super.getY(), width - SPRITE_WIDTH_AREA, height, INPUT_COLOR);
        Tuple<Integer, Integer> pos = getRemovePos();
        context.blitSprite(RenderPipelines.GUI_TEXTURED, CROSS, pos.getA(), pos.getB(), SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (input.isAllowedChatCharacter() || isValid(input)) {
            this.insertText(input.codepointAsString());
            return true;
        } else {
            return false;
        }
    }


    private boolean isValid(CharacterEvent input) {
        return input.codepointAsString().contains("§");
    }

    /**
     * A slightly modified version of the TextFieldWidget write(String)
     * because it removes chars like §
     *
     * @param text The text to add
     */
    @Override
    public void insertText(String text) {
        int i = Math.min(getCursorPosition(), this.selectionEnd);
        int j = Math.max(getCursorPosition(), this.selectionEnd);
        int k = this.maxLength - getValue().length() - (i - j);
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
            String string2 = new StringBuilder(getValue()).replace(i, j, string).toString();
            setValue(string2);
            this.setCursorPosition(i + l);
            this.setHighlightPos(getCursorPosition());
            changedListener.accept(getValue());
        }
    }

    @Override
    public void setHighlightPos(int index) {
        this.selectionEnd = Mth.clamp(index, 0, getValue().length());
        super.setHighlightPos(index);
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        super.setMaxLength(maxLength);
    }

    @Override
    public void setResponder(Consumer<String> changedListener) {
        this.changedListener = changedListener;
        super.setResponder(changedListener);
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
    public boolean isBordered() {
        return false;
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
            parent.removeFilter(index);
            return;
        }

        //make sure you cant click around the button accidentally
        if (width + getX() - SPRITE_WIDTH_AREA < x) return;
        super.onClick(click, doubled);
    }
}
