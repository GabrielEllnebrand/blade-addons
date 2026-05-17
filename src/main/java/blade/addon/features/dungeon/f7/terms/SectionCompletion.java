package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SectionCompletion {

    private static long prevTime = 0;

    public static void init() {
        Events.ON_SECTION_CHANGE.register(() -> {
            Misc.sendSound(Floor7.sectionChangeSound);
            prevTime = System.currentTimeMillis();
            return false;
        });
    }

    public static boolean display() {
        return System.currentTimeMillis() - prevTime < 1000 && Floor7.sectionCompletionNotification;
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        RenderUtils.drawCenteredText(graphics, component, Component.literal("Section completed!").withStyle(ChatFormatting.GREEN));
    }

}
