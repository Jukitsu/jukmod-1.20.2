package net.jukitsumc.jukmod.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin extends Goal {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Shadow
    private int ticksUntilNextAttack;

    @Shadow
    private Path path;
    @Shadow
    @Final
    private double speedModifier;
    @Shadow
    private int ticksUntilNextPathRecalculation;

    @Shadow
    @Final
    private boolean followingTargetEvenIfNotSeen;
    @Shadow
    private double pathedTargetX;
    @Shadow
    private double pathedTargetY;
    @Shadow
    private double pathedTargetZ;


    @Shadow
    protected abstract void checkAndPerformAttack(LivingEntity livingEntity);

    /**
     * @author Jukitsu
     * @reason Fix Melee Attack
     */
    @Overwrite
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else {
            this.path = this.mob.getNavigation().createPath(livingEntity, 0);
            if (this.path != null) {
                return true;
            } else {
                return this.mob.isWithinMeleeAttackRange(livingEntity);
            }
        }
    }

    /**
     * @author Jukitsu
     * @reason Fix Attack Speed
     */
    @Overwrite
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
    }

    /**
     * @author Jukitsu
     * @reason Complete Rewrite
     */
    @Overwrite
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingEntity.getX();
                this.pathedTargetY = livingEntity.getY();
                this.pathedTargetZ = livingEntity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.distanceToSqr(livingEntity);
                if (d > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingEntity);
        }
    }

    /**
     * @author Jukitsu
     * @reason Fix ADHD Mobs
     */
    @Overwrite
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.mob.isWithinRestriction(livingEntity.blockPosition())) {
            return false;
        } else {
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
        }
    }

    /**
     * @author Jukitsu
     * @reason Boost Entities
     */
    @Overwrite
    public void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getAttackInterval();
    }

    /**
     * @author Jukitsu
     * @reason Introduce Attack Speed Based Interval
     */
    @Overwrite
    public int getAttackInterval() {
        return this.adjustedTickDelay(Math.max(10, Mth.floor(1.0D / this.mob.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D)));
    }
}
