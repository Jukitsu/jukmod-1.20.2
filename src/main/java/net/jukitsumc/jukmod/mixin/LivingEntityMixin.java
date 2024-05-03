package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private BooleanOption oldPlayerBackwardsOption = Jukmod.getInstance().getConfig().animations().oldPlayerBackwards();

    @Unique
    private BooleanOption oldClientMovement = Jukmod.getInstance().getConfig().entities().oldClientMovement();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void knockback(double d, double e, double f);

    @ModifyExpressionValue(method="travel", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z"))
    private boolean oldClientMovement(boolean b) {
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
