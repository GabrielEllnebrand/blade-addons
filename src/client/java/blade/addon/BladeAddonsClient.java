package blade.addon;

import blade.addon.rendering.Render;
import blade.addon.utility.Commands;
import blade.addon.utility.Keybinds;
import blade.addon.utility.Location;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class BladeAddonsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Commands.registerCommands();
		Keybinds.register();

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			BushScan.tick(client);
			Keybinds.checkInputs(client);
			FillHelper.tick(client);
			BugScan.tick(client);
		});

		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(Render::beforeDebugRendering);
		Location.init();
	}
}