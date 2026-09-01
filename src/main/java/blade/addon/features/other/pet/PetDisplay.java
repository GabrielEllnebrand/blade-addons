package blade.addon.features.other.pet;

import blade.addon.utils.config.values.ExtraOptions;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PetDisplay extends HUDComponent {

    public PetDisplay() {
        super( "Selected pet display");
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public boolean editable() {
        return ExtraOptions.drawPetHUD;
    }

    @Override
    public boolean shouldRender() {
        return ExtraOptions.drawPetHUD;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int x = getScaledX();
        int y = getScaledY();

        if (SelectedPet.spriteId != null && ExtraOptions.includePetSprite) {
            guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, SelectedPet.spriteId, x, y, 16, 16, 0xffffffff);
        }

        Font textRenderer = Minecraft.getInstance().font;

        int textX = x + (ExtraOptions.includePetSprite ? 18 : 0);
        int textY = y + getHeight() - textRenderer.lineHeight;

        if (ExtraOptions.displayPetLevel && SelectedPet.currentPetText != SelectedPet.NO_PET) {
            guiGraphicsExtractor.text(textRenderer, Component.literal("§7[Lvl " + (SelectedPet.currentPetLevel != -1 ? SelectedPet.currentPetLevel : "???") + "]"), textX, textY, 0xffffffff, true);
            textY -= textRenderer.lineHeight;
        }

        guiGraphicsExtractor.text(textRenderer, SelectedPet.currentPetText, textX, textY, 0xffffffff, true);
    }
}
