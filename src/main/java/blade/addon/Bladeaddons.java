package blade.addon;

import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.utils.dungeon.Phase;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.utils.Keybinds;
import blade.addon.utils.Location;
import blade.addon.utils.config.Config;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class Bladeaddons implements ModInitializer {
	@Override
	public void onInitialize() {
		Config.manager.load();
		Keybinds.register();
		Location.init();

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			Keybinds.checkInputs(client);
			StormTickTimer.tick(client);
			GoldorTickTimer.tick(client);
			Phase.tick(client);
		});

		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			if (Location.inDungeon()) {
				Phase.parseMessage(message);
				LeapMessage.parseMessage(message);
			}
		});
	}
}