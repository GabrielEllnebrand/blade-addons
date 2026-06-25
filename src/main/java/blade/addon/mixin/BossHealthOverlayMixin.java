package blade.addon.mixin;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.rendering.RenderUtils;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Redirect(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/LerpingBossEvent;getName()Lnet/minecraft/network/chat/Component;")
    )
    private Component modifyBossBarText(LerpingBossEvent bossBar) {
        if (!Dungeons.bossHealthNumbers) return bossBar.getName();

        try {
            Component text = bossBar.getName();
            String string = text.getString();

            String name = string.replaceAll("§.", "");
            float health;

            switch (name) {
                case "Maxor":
                    health = 8e8f;
                    break;
                case "Storm":
                    health = 1e9f;
                    break;
                case "Goldor":
                    health = 1.2e9f;
                    break;
                case "Necron":
                    health = 1.4e9f;
                    break;
                default:
                    return text;
            }

            float currHealth = health * bossBar.getProgress();
            return Component.literal("§c" + name + " §a" + RenderUtils.formatNumber(currHealth) + "§7/§a" + RenderUtils.formatNumber(health));
        } catch (Exception e) {
            Debug.LOGGER.error("Failed to modify bossbar name!", e);
            return bossBar.getName();
        }
    }


}
