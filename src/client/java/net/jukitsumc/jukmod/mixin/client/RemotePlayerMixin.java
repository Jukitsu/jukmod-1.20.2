package net.jukitsumc.jukmod.mixin.client;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemotePlayer.class)
public class RemotePlayerMixin {

    @Shadow protected int lerpDeltaMovementSteps;
    @Inject(method="lerpMotion", at=@At("TAIL"))
    public void properlyLerpMotion(double d, double e, double f, CallbackInfo info) {
        this.lerpDeltaMovementSteps = 2;
    }
}
