package blade.addon;

import blade.addon.features.dungeon.*;
import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.features.dungeon.f7.PredevTimer;
import blade.addon.features.dungeon.f7.dragons.DragonHealth;
import blade.addon.features.dungeon.f7.dragons.DragonSpawn;
import blade.addon.features.dungeon.f7.dragons.DragonTracer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.features.dungeon.f7.invincibility.MaskHighlight;
import blade.addon.features.dungeon.f7.location.PositionMessages;
import blade.addon.features.dungeon.f7.maxor.crystals.CrystalSpawn;
import blade.addon.features.dungeon.f7.relic.RelicSpawn;
import blade.addon.features.dungeon.f7.storm.StormTime;
import blade.addon.features.dungeon.f7.storm.pillar.PillarExplode;
import blade.addon.features.dungeon.f7.terms.device.DeviceNotifier;
import blade.addon.features.filter.Filters;
import blade.addon.features.highlight.*;
import blade.addon.features.item.*;
import blade.addon.features.notifications.Notifications;
import blade.addon.features.other.*;
import blade.addon.features.other.arrow.ArrowSwapper;
import blade.addon.features.other.loadout.LoadoutData;
import blade.addon.features.other.pet.SelectedPet;
import blade.addon.features.sound.BonzoSound;
import blade.addon.features.sound.TubaSound;
import blade.addon.utils.Commands;
import blade.addon.utils.Keybinds;
import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.Config;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.Buttons;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.data.PartyUtil;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.events.CustomEvents;
import blade.addon.utils.rendering.RenderingEvents;
import net.fabricmc.api.ModInitializer;

public class Bladeaddons implements ModInitializer {
    @Override
    public void onInitialize() {
        FolderUtility.init();
        Categories.init();
        Components.init();
        Buttons.init();
        try {
            Config.manager.load();
        }catch (NullPointerException err) {}
        Keybinds.init();
        CustomEvents.init();
        Commands.init();
        Debug.init();
        Location.init();
        Phase.init();
        Section.init();
        DungeonClass.init();

        PartyUtil.init();
        EntityUtil.init();

        RenderingEvents.init();

        StormTime.init();
        PositionMessages.init();
        InvincibilityTimer.init();
        LeapMessage.init();
        ExplosiveShot.init();
        HidePlayers.init();
        CrystalSpawn.init();
        RelicSpawn.init();
        AutoRequeue.init();
        MobHighlight.init();
        Scheduler.init();
        ItemHighlight.init();
        BossWaypoints.init();
        DianaNotifier.init();
        DragonSpawn.init();
        SelectedPet.init();
        PredevTimer.init();
        DeviceNotifier.init();
        PillarExplode.init();
        PracticeSS.init();
        CompactHoppity.init();
        DropAnimation.init();
        MimicHighlight.init();
        SheepHighlight.init();
        Notifications.init();
        BatHighlight.init();
        WitherHighlight.init();
        TeammateHighlight.init();
        BonzoSound.init();
        Filters.init();
        DragonHealth.init();
        ArrowSwapper.init();
        StarCountHighlight.init();
        MaskHighlight.init();
        ItemRarityHighlight.init();
        ProtectItem.init();
        SelectedPetHighlight.init();
        SearchBar.init();
        TubaSound.init();
        DragonTracer.init();
        DeathMessage.init();
        BlockPetMenu.init();
        ToolTip.init();
        CompactDamageNumbers.init();
        LoadoutData.init();
        RerollBlocker.init();
    }
}