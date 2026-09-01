package blade.addon.features.dungeon.f7.storm.pillar;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Floor7;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PillarNotification extends CombineableNotification {

    public PillarNotification() {
        super("Pillar explode timer");
    }

    @Override
    public boolean enabled() {
        return Floor7.notifyStormCrush;
    }

    @Override
    public Component getText() {
        return Component.literal("§6||| §bStorm crushed! §6|||");
    }

    @Override
    public boolean shouldRender() {
        return Floor7.notifyStormCrush && PillarExplode.tick > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }

}
