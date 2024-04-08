package net.jukitsumc.jukmod;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class JukmodHUD implements HudRenderCallback {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        if (!client.getDebugOverlay().showDebugScreen()) {
            int currentFPS = client.getFps();
            drawContext.drawString(this.client.font, String.format("Minecraft 1.20.4, %s", currentFPS), 2, 2, 0xffffffff, true);
        }
    }

}
