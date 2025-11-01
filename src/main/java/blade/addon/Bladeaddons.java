package blade.addon;

import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.PositionMessages;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.features.dungeon.TermStartTimer;
import blade.addon.utils.Commands;
import blade.addon.utils.Keybinds;
import blade.addon.utils.Location;
import blade.addon.utils.config.Config;
import blade.addon.utils.dungeon.Phase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class Bladeaddons implements ModInitializer {
	@Override
	public void onInitialize() {
		Config.manager.load();
		Keybinds.register();
        Commands.register();
		Location.init();

        Phase.init();
        StormTickTimer.init();
        GoldorTickTimer.init();
        TermStartTimer.init();
        DeathTickTimer.init();

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			Keybinds.checkInputs(client);
            PositionMessages.tick(client);
			Phase.tick(client);
		});

		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			if (Location.inDungeon()) {
				Phase.parseMessage(message);
				LeapMessage.parseMessage(message);
                ExplosiveShot.parseMessage(message);
			}
		});
	}

    /**
     * TODO:
     * - bloodcamp dialogue kill time
     * - add !dt and !undt support
     * - add boss waypoints or smth
     * - add gfs thingy
     * - croesus counter
     * - score calc?
     * - timer for crystals in p1 or smth
     * - add split ee2 posmsg
     *
     */
}