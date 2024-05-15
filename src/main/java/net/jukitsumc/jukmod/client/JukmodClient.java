package net.jukitsumc.jukmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class JukmodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JukmodHUD jukmodHUD = new JukmodHUD();
        HudRenderCallback.EVENT.register(jukmodHUD);
    }
}
