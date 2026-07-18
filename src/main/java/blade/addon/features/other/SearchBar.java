package blade.addon.features.other;

import blade.addon.utils.MathParser;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.rendering.DrawEvents;
import blade.addon.utils.rendering.RenderUtils;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class SearchBar {

    private static final int SEARCH_Y = 20;
    private static final int SEARCH_WIDTH = 150;
    private static final int SEARCH_HEIGHT = 20;
    private static EditBox searchBar;
    private static boolean shouldDisplay = false;
    private static String searchTerm = "";
    private static double parsedValue = Double.NaN;

    public static void init() {
        DrawEvents.INVENTORY_SLOT_AFTER.register((context, item, x, y) -> {
            if (shouldDisplay() && !searchTerm.isEmpty() && ExtraOptions.toggleableSearchBar && Double.isNaN(parsedValue)) {
                if (!matches(item)) {
                    context.fill(x, y, x + 16, y + 16, ExtraOptions.searchbarMissColor);
                }

            }
        });
    }

    private static boolean matches(ItemStack item) {
        String name = item.getHoverName().getString().toLowerCase();
        if (name.equals("air")) return false;

        return (name.contains(searchTerm) || ItemUtil.containsIgnoreCaseLore(item, searchTerm));
    }

    public static void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        if (!exists() || !shouldDisplay() || !ExtraOptions.toggleableSearchBar) return;
        searchBar.render(context, mouseX, mouseY, deltaTicks);

        if (!Double.isNaN(parsedValue)) {
            String expression = "  §e= §2" + (ExtraOptions.formatMathResult ? RenderUtils.formatNumber((float) parsedValue) : (long)parsedValue);
            Font textRenderer = Minecraft.getInstance().font;
            if (textRenderer == null) return;

            int textX = searchBar.getX() + textRenderer.width(searchTerm) + 4;
            int textY = searchBar.getY() + (searchBar.getHeight() - 8) / 2;
            context.drawString(textRenderer, expression, textX, textY, 0xffffffff, true);
        }
    }

    public static boolean keyPressed(KeyEvent input) {
        if (!exists() || !ExtraOptions.toggleableSearchBar) return false;

        boolean ctrlIsPressed = (input.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0;

        if (ctrlIsPressed && input.key() == GLFW.GLFW_KEY_F) {
            shouldDisplay = !shouldDisplay;
            return true;
        } else if (shouldDisplay() && searchBar.isFocused()) {
            if (input.key() == GLFW.GLFW_KEY_ENTER) {
                if (!Double.isNaN(parsedValue)) {
                    searchBar.setValue(RenderUtils.formatNumber((float) parsedValue));
                }
            } else if (input.key() != GLFW.GLFW_KEY_ESCAPE) {
                searchBar.keyPressed(input);
                return true;
            }
        }
        return false;
    }

    public static void CharTyped(CharacterEvent input) {
        if (!exists() || !searchBar.isFocused() || !shouldDisplay() || !ExtraOptions.toggleableSearchBar) return;
        searchBar.charTyped(input);
    }

    public static void onMouseClick(MouseButtonEvent click) {
        if (!exists() || !shouldDisplay() || !ExtraOptions.toggleableSearchBar) return;
        searchBar.setFocused(inBounds(click.x(), click.y()));
    }


    public static boolean shouldDisplay() {
        return shouldDisplay;
    }

    private static boolean exists() {
        if (searchBar != null) return true;

        Minecraft mc = Minecraft.getInstance();
        Font textRenderer = mc.font;
        Window window = mc.getWindow();

        if (window == null || textRenderer == null) return false;

        searchBar = new EditBox(textRenderer, (window.getGuiScaledWidth() - SEARCH_WIDTH) / 2, SEARCH_Y, SEARCH_WIDTH, SEARCH_HEIGHT, Component.literal(""));
        searchBar.setResponder(string -> {
            searchTerm = string.toLowerCase();
            parsedValue = MathParser.parseExpression(searchTerm);
        });
        return true;
    }

    private static boolean inBounds(double x, double y) {
        int sx = searchBar.getX();
        int sy = searchBar.getY();
        int sw = searchBar.getWidth();
        int sh = searchBar.getHeight();

        return x >= sx && x <= sx + sw && y >= sy && y <= sy + sh;
    }

}
