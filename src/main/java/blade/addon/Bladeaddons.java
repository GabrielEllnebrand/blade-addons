package blade.addon;

import blade.addon.features.dungeon.CrystalSpawn;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.HidePlayersAfterLeap;
import blade.addon.features.dungeon.InvincibilityTimer;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.LeapMessage;
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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

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

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			Keybinds.checkInputs(client);
            PositionMessages.tick(client);
			Phase.tick(client);
		});
	}

    /**
     * TODO:
     * - bloodcamp dialogue kill time
     * - add !dt and !undt support
     * - add boss waypoints or smth
     * - croesus counter
     * - score calc?
     * - timer for crystals in p1 or smth
     * - add split ee2 posmsg
     *
     * - fix death tick timer
     * add a hide player near ss
     */
}