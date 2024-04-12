package net.jukitsumc.jukmod.mixin;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected double lerpX;
    @Shadow protected double lerpY;
    @Shadow protected double lerpZ;
    @Shadow protected double lerpYRot;
    @Shadow protected double lerpXRot;
    @Shadow protected int lerpSteps;

    private Vec3 lerpDeltaMovement = Vec3.ZERO;
    private int lerpDeltaMovementSteps;
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float forceOldBackwardsAnimations(float f) {
        return 0.0F;
    }



/*
    @Override
    protected void lerpPositionAndRotationStep(int i, double d, double e, double f, double g, double h) {
        double j = 1.0 / (double)i;
        double k = Mth.lerp(j, this.xo, d);
        double l = Mth.lerp(j, this.yo, e);
        double m = Mth.lerp(j, this.zo, f);
        float n = (float)Mth.rotLerp(j, (double)this.yRotO, g);
        float o = (float)Mth.lerp(j, (double)this.xRotO, h);
        this.setPos(k, l, m);
        this.setRot(n, o);
    }

*/

}
