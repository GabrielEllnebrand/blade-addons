package blade.addon.features.other;

import blade.addon.utils.Misc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.function.Supplier;

public class InventoryButton {

    private static final Identifier BUTTON_TEXTURE = Identifier.ofVanilla("widget/button");
    private static final int SIZE = 18;
    private static final ArrayList<InventoryButton> inventoryButtons = new ArrayList<>();

    private final int x;
    private final int y;
    private final int index;
    private final Supplier<String> command;

    public InventoryButton(int x, int y, Supplier<String> command) {
        this.x = x;
        this.y = y;
        this.command = command;
        inventoryButtons.add(this);
        this.index = inventoryButtons.size();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        String str = command.get();
        if (str == null ||str.isEmpty()) return;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUTTON_TEXTURE, x, y, SIZE, SIZE);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int center = textRenderer.getWidth("" + index);
        context.drawText(textRenderer, "" + index, x + (SIZE - center) / 2 + 1, y + (SIZE - textRenderer.fontHeight) / 2 + 1, 0xffffffff, true);
    }

    public void onClick(double mouseX, double mouseY) {
        String str = command.get();
        if (str == null ||str.isEmpty()) return;
        Misc.executeCommand(str);
    }

    public boolean inBounds(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + SIZE && mouseY >= y && mouseY <= y + SIZE;
    }

    public static void parseClicks(double mouseX, double mouseY) {
        for (InventoryButton inventoryButton : inventoryButtons) {
            if (inventoryButton.inBounds(mouseX, mouseY)) {
                inventoryButton.onClick(mouseX, mouseY);
            }
        }
    }

    public static void renderAll(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        for (InventoryButton button : inventoryButtons) {
            button.render(context, mouseX, mouseY, deltaTicks);
        }
    }
}
