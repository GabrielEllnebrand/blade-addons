package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Misc;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SectionCompleteNotification extends CombineableNotification {

    private static long prevTime = 0;

    public SectionCompleteNotification() {
        super( "Section completion");
    }

    public void init() {
        Events.ON_SECTION_CHANGE.register(() -> {
            Misc.sendSound(Floor7.sectionChangeSound);
            prevTime = System.currentTimeMillis();
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return System.currentTimeMillis() - prevTime < 1000 && Floor7.sectionCompletionNotification;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public boolean enabled() {
        return Floor7.sectionCompletionNotification;
    }

    @Override
    public Component getText() {
        return Component.literal("Section completed!").withStyle(ChatFormatting.GREEN);
    }
}
