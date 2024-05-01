package net.jukitsumc.jukmod.mixin.client;

import com.mojang.authlib.GameProfile;
import net.jukitsumc.jukmod.Jukmod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemotePlayer.class)
public class RemotePlayerMixin extends AbstractClientPlayer {

    public RemotePlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    /**
     * @author Jukitsu
     * @reason Linear Interpolation doesn't seem to affect rendering. We'll just bring back the old behaviour.
     */
    @Inject(method="lerpMotion", at=@At("HEAD"), cancellable = true)
    public void onLerpMotion(double d, double e, double f, CallbackInfo ci) {
        if (!Jukmod.getInstance().getConfig().entities().lerpPlayerVelocity().get()) {
            this.setDeltaMovement(new Vec3(d, e, f));
            ci.cancel();
        }
    }
}
