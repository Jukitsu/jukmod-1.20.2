package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.KohiConstants;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private BooleanOption oldPlayerBackwardsOption;

    @Unique
    private BooleanOption oldClientMovement;

    @Unique
    private LongSliderOption knockbackType;

    @Inject(method="<init>", at=@At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldPlayerBackwardsOption = Jukmod.getInstance().getConfig().animations().oldPlayerBackwards();
        oldClientMovement = Jukmod.getInstance().getConfig().entities().oldClientMovement();
        knockbackType = Jukmod.getInstance().getConfig().gameplay().knockbackType();
    }

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract double getAttributeValue(Attribute attribute);

    @Shadow
    public abstract void knockback(double d, double e, double f);

    @Inject(method="knockback", at=@At("HEAD"), cancellable = true)
    public void addBackKohiKnockback(double amount, double x, double z, CallbackInfo ci) {
        if (knockbackType.get() == 0) { // KOHI KNOCKBACK
            amount *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (amount > 0.0D) {
                this.hasImpulse = true;
                Vec3 velocity = this.getDeltaMovement();
                Vec3 knockbackVector = (new Vec3(x, 0.0D, z)).normalize().scale(KohiConstants.knockbackHorizontal);
                this.setDeltaMovement(
                        velocity.x / KohiConstants.knockbackFriction - knockbackVector.x,
                        Math.min(KohiConstants.knockbackVerticalLimit, velocity.y / 2.0D + KohiConstants.knockbackVertical),
                        velocity.z / KohiConstants.knockbackFriction - knockbackVector.z
                );
            }
            ci.cancel();
        } else if (knockbackType.get() == -1) { // 1.8 Knockback
            amount *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (amount > 0.0D) {
                this.hasImpulse = true;
                Vec3 velocity = this.getDeltaMovement();
                Vec3 knockbackVector = (new Vec3(x, 0.0D, z)).normalize().scale(amount);
                this.setDeltaMovement(
                        velocity.x / 2.0D - knockbackVector.x,
                        Math.min(0.4D, velocity.y / 2.0D + amount),
                        velocity.z / 2.0D - knockbackVector.z
                );
            }
            ci.cancel();
        }

    }


    @ModifyExpressionValue(method="travel", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z"))
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
