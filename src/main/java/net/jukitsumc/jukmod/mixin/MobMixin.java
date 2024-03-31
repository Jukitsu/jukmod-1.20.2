package net.jukitsumc.jukmod.mixin;

import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    @Shadow
    @Final
    private BodyRotationControl bodyRotationControl;

    @Shadow @Final protected GoalSelector targetSelector;

    @Shadow public abstract int getMaxHeadYRot();
    protected MobMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method="<init>", at=@At("TAIL"))
    public void targetHumanIfHostile(EntityType<? extends Mob> entityType, Level level, CallbackInfo info) {
        if (level != null && !level.isClientSide && !entityType.getCategory().isFriendly()) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal((Mob)(Object)this, Human.class, true));
        }
    }

    @Overwrite
    public float tickHeadTurn(float f, float g) {
        float h = Mth.wrapDegrees(f - this.yBodyRot);
        this.yBodyRot += h * 0.3F;
        float i = Mth.wrapDegrees(this.yHeadRot - this.yBodyRot);
        if (Math.abs(i) > this.getMaxHeadYRot()) {
            this.yBodyRot += i - (float) (Mth.sign(i) * this.getMaxHeadYRot());
        }

        boolean bl = i < -90.0F || i >= 90.0F;
        if (bl) {
            g *= -1.0F;
        }


        this.bodyRotationControl.clientTick();


        return g;
    }
}
