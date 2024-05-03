package net.jukitsumc.jukmod.mixin.client;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Unique
    private LongSliderOption entityLerpSteps = Jukmod.getInstance().getConfig().entities().entityLerpSteps();


    @Redirect(method = "handleMoveEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V"))
    private void properlyLerpEntityMovement(Entity entity,
                                            double x, double y, double z,
                                            float g, float h,
                                            int three) {
        int lerpSteps = (int)(long)this.entityLerpSteps.get();
        entity.lerpTo(x, y, z, g, h, lerpSteps);
    }

    @Redirect(method = "handleRotateMob", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;lerpHeadTo(FI)V"))
    private void properlyLerpEntityRotation(Entity entity, float yRot, int three) {
        int lerpSteps = (int)(long)this.entityLerpSteps.get();;
        entity.lerpHeadTo(yRot, lerpSteps);
    }
}
