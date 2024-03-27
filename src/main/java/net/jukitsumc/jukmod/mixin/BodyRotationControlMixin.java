package net.jukitsumc.jukmod.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BodyRotationControl.class)
public abstract class BodyRotationControlMixin {

    @Shadow
    @Final
    private Mob mob;

    @Redirect(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F"))
    public float clientTick(Mob mob) {
        return mob.yBodyRot;
    }
}
