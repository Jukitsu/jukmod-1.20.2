package net.jukitsumc.jukmod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public class RangedBowAttackGoalMixin<T extends Monster & RangedAttackMob> {
    @Shadow
    @Final
    private T mob;

    @Inject(method = "tick", at = @At("TAIL"))
    public void fixSkeletonStrafing(CallbackInfo info) {
        LivingEntity livingEntity = this.mob.getTarget();
        this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
    }
}
