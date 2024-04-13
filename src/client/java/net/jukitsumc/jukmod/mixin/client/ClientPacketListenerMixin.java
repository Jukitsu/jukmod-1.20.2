package net.jukitsumc.jukmod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    private final Minecraft minecraft = Minecraft.getInstance();

    @Redirect(method = "handleMoveEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V"))
    private void properlyLerpEntityMovement(Entity entity,
                                            double x, double y, double z,
                                            float g, float h,
                                            int three) {
        int lerpSteps = this.minecraft.isSingleplayer() ? 1 : entity.getType().updateInterval();
        entity.lerpTo(x, y, z, g, h, lerpSteps);
    }

    @Redirect(method = "handleRotateMob", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;lerpHeadTo(FI)V"))
    private void properlyLerpEntityRotation(Entity entity, float yRot, int three) {
        int lerpSteps = this.minecraft.isSingleplayer() ? 1 : entity.getType().updateInterval();
        entity.lerpHeadTo(yRot, lerpSteps);
    }
}
