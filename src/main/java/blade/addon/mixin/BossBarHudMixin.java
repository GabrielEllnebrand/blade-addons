package blade.addon.mixin;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.rendering.RenderUtils;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;")
    )
    private Text modifyBossBarText(ClientBossBar bossBar) {
        if (!Dungeons.bossHealthNumbers) return bossBar.getName();

        try {
            Text text = bossBar.getName();
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

            float currHealth = health * bossBar.getPercent();
            return Text.literal("§c" + name + " §a" + RenderUtils.formatNumber(currHealth) + "§7/§a" + RenderUtils.formatNumber(health));
        } catch (Exception e) {
            Debug.LOGGER.error("Failed to modify bossbar name!", e);
            return bossBar.getName();
        }
    }


}
