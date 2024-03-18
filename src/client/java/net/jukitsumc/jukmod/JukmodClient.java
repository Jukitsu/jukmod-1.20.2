package net.jukitsumc.jukmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.jukitsumc.jukmod.JukmodHUD;

public class JukmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		JukmodHUD jukmodHUD = new JukmodHUD();
		HudRenderCallback.EVENT.register(jukmodHUD);
	}
}