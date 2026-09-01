package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class InvincibilityDisplay extends HUDComponent {

    private static final Identifier BONZO_SPRITE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "bonzo-mask");
    private static final Identifier SPIRIT_SPRITE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "spirit-mask");
    private static final Identifier PHOENIX_SPRITE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "phoenix");

    private static final int TEXT_HEIGHT = 9;
    private static final int SPRITE_SIZE = TEXT_HEIGHT;


    public InvincibilityDisplay() {
        super("Invincibility timer");
    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 28;
    }

    @Override
    public boolean editable() {
        return Dungeons.displayInvincibilityTimer;
    }

    @Override
    public boolean shouldRender() {
        if (!Dungeons.displayInvincibilityTimer || !Location.inDungeon()) {
            return false;
        }

        switch (Dungeons.displayWhen) {
            case BOSS_ONLY -> {
                return Phase.inBoss();
            }
            case P3_ONLY -> {
                return Phase.inP3();
            }
            case ALWAYS -> {
                return true;
            }
            case USEFUL_PHASES -> {
                return Phase.inP3() || Phase.inP2();
            }
            case null, default -> {
                return false;
            }
        }
    }

    @Override
    public List<HUDCategory> categories() {
         switch (Dungeons.displayWhen) {
            case BOSS_ONLY -> {
                return List.of(Categories.P1, Categories.P2, Categories.P3, Categories.P4, Categories.P5);
            }
            case P3_ONLY -> {
                return List.of(Categories.P3);
            }
            case ALWAYS -> {
                return null;
            }
            case USEFUL_PHASES -> {
                return List.of(Categories.P2, Categories.P3);
            }
        }

        return null;
    }

    private static Component formatTimer(int ticks) {
        return Component.literal("§8(§7" + Constants.DECIMAL_FORMAT.format(ticks * Constants.TICK_DURATION) + "§8)");
    }

    private static Component getText(int ticks, boolean isOn, String string) {
        if (ticks > 0) {
            return Component.literal("§c" + string).append(formatTimer(ticks));
        } else {
            String color = isOn ? "§e" : "§a";
            return Component.literal(color + string);
        }
    }

    private static void drawSprite(GuiGraphicsExtractor context, Identifier identifier, int x, int y, boolean isOn, int ticks, Component timerText) {
        int color = (ticks > 0 ? Constants.RED : isOn ? Constants.YELLOW : Constants.GREEN);
        context.fill(x, y, x + SPRITE_SIZE, y + SPRITE_SIZE, color);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x, y, SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
        if (ticks > 0) {
            context.text(Minecraft.getInstance().font, timerText, x + TEXT_HEIGHT * 2, y, 0xffffffff, true);
        }

    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int x = getScaledX();
        int y = getScaledY();

        if (Dungeons.useSprites) {
            drawSprite(guiGraphicsExtractor, BONZO_SPRITE, x, y, InvincibilityTimer.bonzoMaskOn, InvincibilityTimer.bonzoMaskTicks, formatTimer(InvincibilityTimer.bonzoMaskTicks));
            drawSprite(guiGraphicsExtractor, SPIRIT_SPRITE, x, y + SPRITE_SIZE + 1, InvincibilityTimer.spiritMaskOn, InvincibilityTimer.spiritMaskTicks, formatTimer(InvincibilityTimer.spiritMaskTicks));
            drawSprite(guiGraphicsExtractor, PHOENIX_SPRITE, x, y + (SPRITE_SIZE + 1) * 2, InvincibilityTimer.phoenixOn, InvincibilityTimer.phoenixTicks, formatTimer(InvincibilityTimer.phoenixTicks));
        } else {
            if (Dungeons.removeMaskPart) {
                guiGraphicsExtractor.text(Minecraft.getInstance().font, getText(InvincibilityTimer.bonzoMaskTicks, InvincibilityTimer.bonzoMaskOn, "Bonzo "), x, y, 0xffffffff, true);
                guiGraphicsExtractor.text(Minecraft.getInstance().font, getText(InvincibilityTimer.spiritMaskTicks, InvincibilityTimer.spiritMaskOn, "Spirit "), x, y + TEXT_HEIGHT, 0xffffffff, true);
            } else {
                guiGraphicsExtractor.text(Minecraft.getInstance().font, getText(InvincibilityTimer.bonzoMaskTicks, InvincibilityTimer.bonzoMaskOn, "Bonzo's Mask "), x, y, 0xffffffff, true);
                guiGraphicsExtractor.text(Minecraft.getInstance().font, getText(InvincibilityTimer.spiritMaskTicks, InvincibilityTimer.spiritMaskOn, "Spirit Mask "), x, y + TEXT_HEIGHT, 0xffffffff, true);
            }
            guiGraphicsExtractor.text(Minecraft.getInstance().font, getText(InvincibilityTimer.phoenixTicks, InvincibilityTimer.phoenixOn, "Phoenix "), x, y + TEXT_HEIGHT * 2, 0xffffffff, true);
        }
    }
}
