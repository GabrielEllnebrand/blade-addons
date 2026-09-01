package blade.addon.features.dungeon.f7.maxor.crystals;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Floor7;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CrystalNotification extends CombineableNotification {

    public CrystalNotification() {
        super("Crystal reminder notification");
    }

    @Override
    public boolean enabled() {
        return Floor7.crystalPlaceReminder;
    }

    @Override
    public Component getText() {
        return Component.literal("§bPlace Crystal!");
    }

    @Override
    public boolean shouldRender() {
        return CrystalSpawn.displayNotification();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P1);
    }
}
