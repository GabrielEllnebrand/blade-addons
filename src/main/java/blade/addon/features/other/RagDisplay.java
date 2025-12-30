package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.WolfSoundVariants;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RagDisplay {

    private static final SoundEvent SOUND =SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).deathSound().value();
    private static final int TOTAL_TICKS = 200;

    private static int tick = 0;

    public static void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (!ExtraOptions.enableRagaxeDisplay && !ExtraOptions.useCustomRagSound) return false;
            if (soundEvent != SOUND) return false;
            if (volume != 1 && pitch != 1.4920635223388672) return false;

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return false;

            if (!player.getMainHandStack().getName().getString().contains("Ragnarock")) return false;

            if (ExtraOptions.enableRagaxeDisplay) {
                tick = TOTAL_TICKS;
            }

            if (ExtraOptions.useCustomRagSound) {
                Scheduler.scheduleSound(ExtraOptions.ragSound);
                return true;
            }
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> tick = Math.max(tick - 1, 0));
    }

    public static boolean display() {
        return tick > 0;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(tick * Constants.TICK_DURATION)).formatted(Formatting.YELLOW), x, y, component.getWidth());

    }

}
