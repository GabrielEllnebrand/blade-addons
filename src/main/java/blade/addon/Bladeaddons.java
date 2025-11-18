package blade.addon;

import blade.addon.features.dungeon.AutoRequeue;
import blade.addon.features.dungeon.ChestCounter;
import blade.addon.features.dungeon.CrystalSpawn;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.HidePlayersAfterLeap;
import blade.addon.features.dungeon.InvincibilityTimer;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.MobHighlight;
import blade.addon.features.dungeon.PositionMessages;
import blade.addon.features.dungeon.RelicTimer;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.features.dungeon.TermStartTimer;
import blade.addon.features.dungeon.WarpCooldown;
import blade.addon.utils.Commands;
import blade.addon.utils.Keybinds;
import blade.addon.utils.Location;
import blade.addon.utils.config.Config;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import net.fabricmc.api.ModInitializer;

public class Bladeaddons implements ModInitializer {
	@Override
	public void onInitialize() {
		Config.manager.load();
		Keybinds.register();
        Commands.register();
		Location.init();
        Phase.init();
        DungeonClass.init();
        StormTickTimer.init();
        GoldorTickTimer.init();
        TermStartTimer.init();
        DeathTickTimer.init();
        PositionMessages.init();
        InvincibilityTimer.init();
        WarpCooldown.init();
        LeapMessage.init();
        ExplosiveShot.init();
        DupeClassChecker.init();
        HidePlayersAfterLeap.init();
        CrystalSpawn.init();
        KeyNotifier.init();
        RelicTimer.init();
        AutoRequeue.init();
        MobHighlight.init();
        ChestCounter.init();
	}
}