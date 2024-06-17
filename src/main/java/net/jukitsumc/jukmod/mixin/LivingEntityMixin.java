package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private BooleanOption oldPlayerBackwardsOption;

    @Unique
    private BooleanOption oldClientMovement;

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldPlayerBackwardsOption = Jukmod.getInstance().getConfig().animations().oldPlayerBackwards();
        oldClientMovement = Jukmod.getInstance().getConfig().entities().oldClientMovement();
    }

    @Shadow
    public abstract void knockback(double d, double e, double f);

    @Shadow public float yHeadRot;
    @Shadow public float yBodyRot;

    @Shadow protected abstract float getMaxHeadRotationRelativeToBody();

    @Inject(method = "tickHeadTurn", at = @At("TAIL"), cancellable = true)
    public void tickHeadTurn(float f, float g, CallbackInfoReturnable ci) {
        if (oldPlayerBackwardsOption.get()) {
            float i = Mth.wrapDegrees(f - this.yBodyRot);
            this.yBodyRot += i * 0.3F;
            float j = Mth.wrapDegrees(this.yHeadRot - this.yBodyRot);
            boolean bl = j < -90.0F || j >= 90.0F;
            if (Mth.abs(j) > 75.0F) {
                j = 75.0F * Mth.sign(j);
            }
            this.yBodyRot = this.yHeadRot - j;

            if (Mth.abs(j) > getMaxHeadRotationRelativeToBody())
            {
                this.yBodyRot += j * 0.2F;
            }

            if (bl) {
                g *= -1.0F;
            }

            ci.setReturnValue(g);
        }

    }


    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z"))
    private boolean addOldClientMovement(boolean b) {
        return this.oldClientMovement.get() || b;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float forceOldBackwardsAnimations(float f) {
        return this.oldPlayerBackwardsOption.get() ? 0.0F : f;
    }


    /**
     * @author Jukitsu
     * @reason Fixes MC-147694
     */
    @Overwrite
    public void blockedByShield(LivingEntity livingEntity) {
        this.knockback(0.5, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
    }


}
